from __future__ import annotations

import argparse
import json
import re
import time
from pathlib import Path

import javalang
from tqdm import tqdm

import adapt_agent


ROOT = Path(__file__).resolve().parent
DEFAULT_INPUT = ROOT / "data" / "adaptations_with_snapshots_with_intent.json"
DEFAULT_OUTPUT = ROOT / "data" / "adaptations_with_snapshots_with_intent_and_results.json"
DEFAULT_DATASET_ROOT = ROOT / "data" / "adaptation-dataset"


class LoggingLLM(adapt_agent.LLMClient):
    def __init__(self, max_retries: int = 3):
        super().__init__(max_retries=max_retries)
        self.last_raw: str | None = None

    def complete_json(self, system_prompt: str, user_prompt: str) -> dict:
        last_err: Exception | None = None
        for _ in range(self.max_retries):
            raw = self.complete(system_prompt, user_prompt)
            self.last_raw = raw
            cleaned = raw.replace("```json", "").replace("```", "")
            try:
                return json.loads(cleaned)
            except Exception as exc:
                last_err = exc
        raise RuntimeError(f"LLM JSON parse failed after retries: {last_err}")


def _load_json(path: Path) -> list[dict]:
    return json.loads(path.read_text(encoding="utf-8"))


def _write_json(path: Path, data: list[dict]) -> None:
    tmp = path.with_suffix(path.suffix + ".tmp")
    tmp.write_text(json.dumps(data, indent=2, ensure_ascii=True), encoding="utf-8")
    tmp.replace(path)


def _index_dataset_folders(dataset_root: Path) -> dict[str, Path]:
    mapping: dict[str, Path] = {}
    duplicates = set()
    for folder in dataset_root.iterdir():
        if not folder.is_dir() or not folder.name.startswith("so-"):
            continue
        for so_file in folder.glob("so-*.java"):
            key = so_file.stem
            if key in mapping and mapping[key] != folder:
                duplicates.add(key)
                continue
            mapping[key] = folder
    if duplicates:
        print(f"Warning: duplicate so_key entries: {len(duplicates)}")
    return mapping


def _write_outputs(folder: Path, gh_id: str, code: str, response_raw: str) -> None:
    result_path = folder / f"adapt_agent_result_{gh_id}.java"
    response_path = folder / f"adapt_agent_response_{gh_id}.json"
    result_path.write_text(code, encoding="utf-8")
    response_path.write_text(response_raw, encoding="utf-8")


def _normalize_indentation(code: str, indent: str) -> str:
    lines = code.strip("\n").splitlines()
    if not lines:
        return ""
    non_empty = [line for line in lines if line.strip()]
    min_indent = min((len(line) - len(line.lstrip())) for line in non_empty) if non_empty else 0
    normalized = [line[min_indent:] if len(line) >= min_indent else "" for line in lines]
    return "\n".join((indent + line if line.strip() else indent + line) for line in normalized)


def _replace_snippet(file_code: str, target_snippet: str, new_snippet: str) -> tuple[str, bool]:
    if not target_snippet or not new_snippet:
        return file_code, False
    # Strip dummy wrapper if present.
    target = target_snippet.replace("public class foo", "class foo")
    target = target.strip()
    new_snippet = new_snippet.strip()
    pattern = re.compile(re.escape(target), re.DOTALL)
    match = pattern.search(file_code)
    if not match:
        return file_code, False
    indent = re.match(r"\s*", match.group(0).splitlines()[0]).group(0)
    replacement = _normalize_indentation(new_snippet, indent)
    start, end = match.span()
    return file_code[:start] + replacement + file_code[end:], True


def _inject_code(file_code: str, code: str, gh_snippet: str | None) -> tuple[str, str | None]:
    # Prefer replacing the exact GitHub snippet when available.
    if gh_snippet:
        replaced, ok = _replace_snippet(file_code, gh_snippet, code)
        if ok:
            return replaced, None
    # Fallback: if TODO placeholder exists, replace it; otherwise return original.
    match = re.search(r"(?m)^(?P<indent>[ \t]*)// TODO\\s*$", file_code)
    if not match:
        return file_code, None
    indent = match.group("indent")
    code_block = _normalize_indentation(code, indent)
    start, end = match.span()
    return file_code[:start] + code_block + file_code[end:], None


def _syntax_check(source: str) -> tuple[bool, str]:
    try:
        javalang.parse.parse(source)
        return True, "parsed"
    except Exception as exc:
        return False, f"javalang parse error: {exc}"


def main() -> None:
    parser = argparse.ArgumentParser(description="Run adapt agent on adaptation dataset.")
    parser.add_argument("--input", type=Path, default=DEFAULT_INPUT, help="Input JSON dataset.")
    parser.add_argument("--output", type=Path, default=DEFAULT_OUTPUT, help="Output JSON dataset.")
    parser.add_argument(
        "--dataset-root",
        type=Path,
        default=DEFAULT_DATASET_ROOT,
        help="Adaptation dataset root folder.",
    )
    parser.add_argument("--start", type=int, default=0, help="Start index in dataset.")
    parser.add_argument("--limit", type=int, default=0, help="Max records to process (0 = all).")
    parser.add_argument("--save-every", type=int, default=10, help="Save output every N items.")
    parser.add_argument("--sleep", type=float, default=0.0, help="Sleep between API calls (seconds).")
    parser.add_argument("--max-attempts", type=int, default=3, help="Max LLM regeneration attempts.")
    parser.add_argument("--verify", action="store_true", help="Enable verification.")
    parser.add_argument("--force", action="store_true", help="Re-run even if outputs exist.")
    args = parser.parse_args()

    data = _load_json(args.input)
    folder_map = _index_dataset_folders(args.dataset_root)

    llm_client = LoggingLLM()
    verifier = adapt_agent.Verifier() if args.verify else None
    pipeline = adapt_agent.AdaptAgentPipeline(llm_client, verifier=verifier)

    processed = 0
    updated = 0
    for idx, record in tqdm(list(enumerate(data)), total=len(data), desc="adapt", unit="items"):
        if idx < args.start:
            continue
        if args.limit and processed >= args.limit:
            break

        if record.get("adapt_agent_code") and not args.force:
            processed += 1
            continue

        so_key = record.get("so_key", "")
        gh_id = record.get("gh_id", "")
        so_snippet = record.get("so_snippet", "")
        file_level = record.get("file_level_code_without_target", "")
        intent = record.get("adaptation_intent", "")

        if not so_snippet or not file_level or not intent:
            # NOTE: This pipeline expects `adaptation_intent` to already be present in the dataset.
            # If it's missing, you can reconstruct intent by fetching the full Stack Overflow post
            # (question + accepted answer) via `util.extract_so_post_api(int(record["so_id"]))`,
            # then passing that `full_post` into the intent summarizer.
            record.pop("adapt_agent_error", None)
            record["adapt_agent_status"] = "skipped"
            record["adapt_agent_failure_reason"] = "missing_inputs"
            processed += 1
            continue

        intent_summary = record.get("adaptation_intent", "").strip()
        policy_checklist = pipeline.policy_agent.build_checklist()
        context_summary = pipeline.context_miner.summarize(
            file_level, file_context=record.get("file_context")
        )
        plan = pipeline.planner.plan(
            so_snippet, intent_summary, context_summary, policy_checklist
        )

        verification_errors = None
        artifacts = None
        response_raw = ""
        failure_reason = ""
        for attempt in range(args.max_attempts):
            try:
                adapted_code, explanation = pipeline.adapter.adapt(
                    intent_summary,
                    plan,
                    "",
                    so_snippet,
                    file_level,
                    context_summary,
                    verification_errors,
                )
                response_raw = llm_client.last_raw or ""
                if not response_raw:
                    response_raw = json.dumps(
                        {"code": adapted_code, "explanation": explanation},
                        indent=2,
                        ensure_ascii=True,
                    )

                integrated, _ = _inject_code(
                    record.get("file_level_code", file_level),
                    adapted_code,
                    record.get("gh_snippet"),
                )
                ok, msg = _syntax_check(integrated)
                record["adapt_agent_integration_check"] = {"ok": ok, "message": msg}
                if ok:
                    verification = (
                        verifier.verify(adapted_code)
                        if verifier is not None
                        else adapt_agent.VerificationResult(True, "not run")
                    )
                    artifacts = adapt_agent.AdaptationArtifacts(
                        intent_summary=intent_summary,
                        policy_checklist=policy_checklist,
                        plan=plan,
                        context_summary=context_summary,
                        adapted_code=adapted_code,
                        explanation=explanation,
                        verification=verification,
                    )
                    break
                verification_errors = msg
                failure_reason = msg
            except Exception as exc:
                failure_reason = f"{type(exc).__name__}: {exc}"
                # Retry on JSON parse failures or other transient LLM errors.
                if "LLM JSON parse failed" in failure_reason:
                    continue
                break

        if artifacts is None:
            record.pop("adapt_agent_error", None)
            record["adapt_agent_status"] = "failed"
            record["adapt_agent_failure_reason"] = failure_reason or "unknown_failure"
            record["adapt_agent_response_raw"] = response_raw or None
            processed += 1
            continue

        record.pop("adapt_agent_error", None)
        record["adapt_agent_status"] = "success"
        record["adapt_agent_failure_reason"] = None
        record["adapt_agent_code"] = artifacts.adapted_code
        record["adapt_agent_explanation"] = artifacts.explanation
        record["adapt_agent_response_raw"] = response_raw
        record["adapt_agent_verification"] = {
            "ok": artifacts.verification.ok,
            "message": artifacts.verification.message,
        }
        folder = folder_map.get(so_key)
        if folder:
            _write_outputs(folder, gh_id, artifacts.adapted_code, response_raw)
        else:
            record["adapt_agent_status"] = "failed"
            record["adapt_agent_failure_reason"] = f"folder not found for so_key {so_key}"
        updated += 1

        processed += 1
        if args.save_every and processed % args.save_every == 0:
            _write_json(args.output, data)
            print(f"Saved {processed} / {len(data)} (updated {updated})")
        if args.sleep:
            time.sleep(args.sleep)

    _write_json(args.output, data)
    print(f"Done. Processed {processed}, updated {updated}, total {len(data)}")


if __name__ == "__main__":
    main()
