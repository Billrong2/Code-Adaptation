#!/usr/bin/env python3
from __future__ import annotations

import argparse
import json
import math
import re
from collections import Counter, defaultdict
from pathlib import Path
from typing import Any, Dict, List, Optional, Tuple

from tqdm import tqdm
import javalang

ROOT = Path(__file__).resolve().parents[1]
DEFAULT_DATASET = ROOT / "data" / "adaptations_with_snapshots_with_intent_and_results.json"
DEFAULT_ADAPT_TESTS = ROOT / "testing" / "result_test_case_adapt_agent.json"
DEFAULT_ADAPT_JUDGED = ROOT / "data" / "adaptations_with_snapshots_with_intent_and_results_llm_judged.json"
DEFAULT_AGENTLESS_MERGED = ROOT / "results" / "agentless" / "agentless_outputs_merged_marked.json"
DEFAULT_AGENTLESS_WITH_TESTS = ROOT / "results" / "agentless" / "with_tests" / "results.json"
DEFAULT_AGENTLESS_PATCH_ROOT = ROOT / "results" / "agentless" / "with_tests"
DEFAULT_OUT_ADAPT = ROOT / "results" / "similarity" / "adaptagent_similarity.json"
DEFAULT_OUT_AGENTLESS = ROOT / "results" / "similarity" / "agentless_similarity.json"

GH_URL_RE = re.compile(
    r"https?://github\\.com/([^/]+)/([^/]+)/blob/([0-9a-fA-F]+)/([^#]+)(?:#L(\\d+)-L(\\d+))?"
)


def _read_json(path: Path) -> List[Dict[str, Any]]:
    return json.loads(path.read_text(encoding="utf-8"))


def _write_json(path: Path, payload: Any) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(payload, indent=2, ensure_ascii=True), encoding="utf-8")


def _strip_dummy_wrapper(snippet: str) -> str:
    lines = snippet.splitlines()
    if not lines:
        return ""
    # find first non-empty and last non-empty
    first_idx = next((i for i, ln in enumerate(lines) if ln.strip()), 0)
    last_idx = len(lines) - 1 - next((i for i, ln in enumerate(reversed(lines)) if ln.strip()), 0)
    first = lines[first_idx].strip()
    last = lines[last_idx].strip()
    if "class " in first and last == "}":
        inner = lines[first_idx + 1 : last_idx]
        return "\n".join(inner).strip()
    return snippet.strip()


def _remove_comments(code: str) -> str:
    # remove /* */ and // comments
    code = re.sub(r"/\\*.*?\\*/", "", code, flags=re.DOTALL)
    code = re.sub(r"//.*?$", "", code, flags=re.MULTILINE)
    return code


def _normalize(code: str) -> str:
    code = _strip_dummy_wrapper(code)
    code = _remove_comments(code)
    code = re.sub(r"\\s+", " ", code)
    return code.strip()


TOKEN_RE = re.compile(
    r"[A-Za-z_][A-Za-z0-9_]*|\\d+|==|!=|<=|>=|&&|\\|\\||[{}();,\\.\\[\\]]|[+*/%<>]=?|\\-=?"
)


def _tokenize(code: str) -> List[str]:
    return TOKEN_RE.findall(code)


def token_bleu(a: str, b: str, max_n: int = 4) -> float:
    a = _normalize(a)
    b = _normalize(b)
    if not a or not b:
        return 0.0
    ta = _tokenize(a)
    tb = _tokenize(b)
    if not ta or not tb:
        return 0.0

    # Brevity penalty
    len_a = len(ta)
    len_b = len(tb)
    bp = 1.0 if len_a > len_b else math.exp(1 - (len_b / max(len_a, 1)))

    def ngrams(tokens: List[str], n: int) -> Counter:
        return Counter(tuple(tokens[i : i + n]) for i in range(len(tokens) - n + 1))

    precisions = []
    for n in range(1, max_n + 1):
        ng_a = ngrams(ta, n)
        ng_b = ngrams(tb, n)
        if not ng_a:
            precisions.append(0.0)
            continue
        overlap = sum(min(count, ng_b.get(ng, 0)) for ng, count in ng_a.items())
        precisions.append((overlap + 1) / (sum(ng_a.values()) + 1))
    # geometric mean
    log_p = sum(math.log(p) for p in precisions if p > 0)
    bleu = bp * math.exp(log_p / max_n)
    return bleu


def levenshtein_sim(a: str, b: str) -> float:
    a = _normalize(a)
    b = _normalize(b)
    if not a and not b:
        return 1.0
    if not a or not b:
        return 0.0
    # DP with two rows
    prev = list(range(len(b) + 1))
    for i, ca in enumerate(a, start=1):
        curr = [i]
        for j, cb in enumerate(b, start=1):
            cost = 0 if ca == cb else 1
            curr.append(min(prev[j] + 1, curr[j - 1] + 1, prev[j - 1] + cost))
        prev = curr
    dist = prev[-1]
    return 1.0 - dist / max(len(a), len(b))


def ast_node_match(a: str, b: str) -> Optional[float]:
    a = _strip_dummy_wrapper(a)
    b = _strip_dummy_wrapper(b)
    if not a or not b:
        return None

    def parse_method_nodes(code: str) -> Counter:
        wrapped = f"public class Dummy {{\n{code}\n}}"
        try:
            tree = javalang.parse.parse(wrapped)
        except Exception:
            return Counter()
        nodes = Counter()
        for _, node in tree:
            # Ignore wrapper nodes; only count nodes inside method bodies.
            if isinstance(node, javalang.tree.MethodDeclaration):
                for _, sub in node:
                    if isinstance(sub, javalang.tree.MethodDeclaration):
                        continue
                    nodes[type(sub).__name__] += 1
        return nodes

    ca = parse_method_nodes(a)
    cb = parse_method_nodes(b)
    if not ca or not cb:
        return None
    all_keys = set(ca) | set(cb)
    inter = sum(min(ca.get(k, 0), cb.get(k, 0)) for k in all_keys)
    union = sum(max(ca.get(k, 0), cb.get(k, 0)) for k in all_keys)
    return inter / union if union else None


def _parse_gh_url(url: str) -> Optional[Tuple[str, str, str, str, Optional[int], Optional[int]]]:
    if not url:
        return None
    match = GH_URL_RE.match(url)
    if not match:
        return None
    owner, repo, sha, file_path, start, end = match.groups()
    return owner, repo, sha, file_path, int(start) if start else None, int(end) if end else None


def _extract_by_lines(text: str, start: int, end: int) -> str:
    lines = text.splitlines()
    if start < 1 or end < start or end > len(lines):
        return ""
    return "\n".join(lines[start - 1 : end])


def _apply_unified_diff(original: str, diff_text: str) -> Optional[str]:
    lines = original.splitlines()
    out: List[str] = []
    cur = 0
    hunk_re = re.compile(r"@@ -(\\d+)(?:,(\\d+))? \\+(\\d+)(?:,(\\d+))? @@")
    diff_lines = diff_text.splitlines()
    i = 0
    while i < len(diff_lines):
        line = diff_lines[i]
        if line.startswith("@@ "):
            m = hunk_re.match(line)
            if not m:
                return None
            start_old = int(m.group(1))
            # copy unchanged lines before hunk
            while cur < start_old - 1:
                out.append(lines[cur])
                cur += 1
            i += 1
            # process hunk lines
            while i < len(diff_lines) and not diff_lines[i].startswith("@@ "):
                hline = diff_lines[i]
                if hline.startswith("\\\\"):
                    i += 1
                    continue
                if hline.startswith(" "):
                    out.append(lines[cur])
                    cur += 1
                elif hline.startswith("-"):
                    cur += 1
                elif hline.startswith("+"):
                    out.append(hline[1:])
                i += 1
            continue
        i += 1
    # append remaining lines
    out.extend(lines[cur:])
    return "\n".join(out)


def _bucket_key(outcome: str, label: str) -> str:
    return f"{outcome}:{label}"


def main() -> None:
    parser = argparse.ArgumentParser(description="Compute CosSim/EditSim for approaches.")
    parser.add_argument("--dataset", default=str(DEFAULT_DATASET), help="AdaptAgent dataset JSON.")
    parser.add_argument(
        "--adapt-tests",
        dest="adapt_tests",
        default=str(DEFAULT_ADAPT_TESTS),
        help="AdaptAgent test results JSON.",
    )
    parser.add_argument(
        "--adapt-judged",
        dest="adapt_judged",
        default=str(DEFAULT_ADAPT_JUDGED),
        help="AdaptAgent LLM judge JSON.",
    )
    parser.add_argument(
        "--agentless-merged",
        dest="agentless_merged",
        default=str(DEFAULT_AGENTLESS_MERGED),
        help="Agentless merged marked JSON.",
    )
    parser.add_argument(
        "--agentless-with-tests",
        dest="agentless_with_tests",
        default=str(DEFAULT_AGENTLESS_WITH_TESTS),
        help="Agentless with-tests results JSON.",
    )
    parser.add_argument("--out", default="", help="Optional output JSON summary.")
    parser.add_argument("--out-adapt", default=str(DEFAULT_OUT_ADAPT), help="Per-entry AdaptAgent similarity output.")
    parser.add_argument("--out-agentless", default=str(DEFAULT_OUT_AGENTLESS), help="Per-entry Agentless similarity output.")
    args = parser.parse_args()

    dataset = _read_json(Path(args.dataset))
    adapt_judged = {
        (r["so_key"], r["gh_id"]): r for r in _read_json(Path(args.adapt_judged))
    }

    agentless = _read_json(Path(args.agentless_merged))
    agentless_with_tests = {
        (r["so_key"], r["gh_id"]): r for r in _read_json(Path(args.agentless_with_tests))
    }

    # Accumulators: approach -> bucket -> list of sims
    summary_edit: Dict[str, Dict[str, List[float]]] = defaultdict(lambda: defaultdict(list))
    summary_bleu: Dict[str, Dict[str, List[float]]] = defaultdict(lambda: defaultdict(list))
    summary_ast: Dict[str, Dict[str, List[float]]] = defaultdict(lambda: defaultdict(list))

    adapt_out: List[Dict[str, Any]] = []
    # AdaptAgent
    for rec in tqdm(dataset, desc="sim(adaptagent)"):
        key = (rec.get("so_key"), rec.get("gh_id"))
        gt = rec.get("gh_snippet") or ""
        pred = rec.get("adapt_agent_code") or ""
        if not gt or not pred:
            adapt_out.append({**rec, "edit_sim": None, "bleu_sim": None, "ast_sim": None})
            continue
        edit = levenshtein_sim(pred, gt)
        judge = adapt_judged.get(key)
        manual = "Corr" if judge and judge.get("adapt_llm_judge_decision") == "Yes" else "Incorr"
        has_tests = bool(rec.get("has_tests_repo"))
        if has_tests:
            test_outcome = "Pass" if manual == "Corr" else "Fail"
        else:
            test_outcome = None

        bleu = token_bleu(pred, gt)
        ast = ast_node_match(pred, gt)

        if test_outcome is not None:
            summary_edit["AdaptAgent"][_bucket_key("test", test_outcome)].append(edit)
            summary_bleu["AdaptAgent"][_bucket_key("test", test_outcome)].append(bleu)
            if ast is not None:
                summary_ast["AdaptAgent"][_bucket_key("test", test_outcome)].append(ast)

        summary_edit["AdaptAgent"][_bucket_key("manual", manual)].append(edit)
        summary_bleu["AdaptAgent"][_bucket_key("manual", manual)].append(bleu)
        if ast is not None:
            summary_ast["AdaptAgent"][_bucket_key("manual", manual)].append(ast)
        adapt_out.append(
            {
                **rec,
                "edit_sim": edit,
                "bleu_sim": bleu,
                "ast_sim": ast,
                "has_tests_repo": has_tests,
                "test_outcome": test_outcome,
                "manual_outcome": manual,
            }
        )

    agentless_out: List[Dict[str, Any]] = []
    # Agentless
    for rec in tqdm(agentless, desc="sim(agentless)"):
        key = (rec.get("so_key"), rec.get("gh_id"))
        gt = rec.get("gh_snippet") or ""
        if not gt:
            agentless_out.append({**rec, "edit_sim": None, "bleu_sim": None, "ast_sim": None})
            continue

        has_tests = bool(rec.get("agentless_has_tests_repo"))
        if not has_tests:
            parsed_repo = _parse_gh_url(rec.get("gh_url") or "")
            if parsed_repo:
                owner, repo, sha, *_ = parsed_repo
                repo_id = f"{owner}__{repo}__{sha}"
                has_tests = repo_id in available_repos
        if has_tests:
            # reconstruct function from patch
            patch_path = DEFAULT_AGENTLESS_PATCH_ROOT / rec["so_key"] / "patch.diff"
            file_level = rec.get("file_level_code") or ""
            gh_url = rec.get("gh_url") or ""
            parsed = _parse_gh_url(gh_url)
            if not file_level or not parsed or not patch_path.exists():
                agentless_out.append({**rec, "edit_sim": None, "bleu_sim": None, "ast_sim": None})
                continue
            patched = _apply_unified_diff(file_level, patch_path.read_text(encoding="utf-8", errors="replace"))
            if not patched:
                agentless_out.append({**rec, "edit_sim": None, "bleu_sim": None, "ast_sim": None})
                continue
            _, _, _, _, start, end = parsed
            if not start or not end:
                agentless_out.append({**rec, "edit_sim": None, "bleu_sim": None, "ast_sim": None})
                continue
            pred = _extract_by_lines(patched, start, end)
        else:
            pred = rec.get("agentless_res") or ""

        if not pred:
            agentless_out.append({**rec, "edit_sim": None, "bleu_sim": None, "ast_sim": None})
            continue
        edit = levenshtein_sim(pred, gt)

        manual_decision = rec.get("agentless_with_tests_decision") if has_tests else rec.get("agentless_llm_judge_decision")
        manual = "Corr" if manual_decision == "Yes" else "Incorr"
        if has_tests:
            test_outcome = "Pass" if manual == "Corr" else "Fail"
        else:
            test_outcome = None

        bleu = token_bleu(pred, gt)
        ast = ast_node_match(pred, gt)

        if test_outcome is not None:
            summary_edit["Agentless"][_bucket_key("test", test_outcome)].append(edit)
            summary_bleu["Agentless"][_bucket_key("test", test_outcome)].append(bleu)
            if ast is not None:
                summary_ast["Agentless"][_bucket_key("test", test_outcome)].append(ast)

        summary_edit["Agentless"][_bucket_key("manual", manual)].append(edit)
        summary_bleu["Agentless"][_bucket_key("manual", manual)].append(bleu)
        if ast is not None:
            summary_ast["Agentless"][_bucket_key("manual", manual)].append(ast)
        agentless_out.append(
            {
                **rec,
                "edit_sim": edit,
                "bleu_sim": bleu,
                "ast_sim": ast,
                "agentless_has_tests_repo": has_tests,
                "test_outcome": test_outcome,
                "manual_outcome": manual,
            }
        )

    # Print summary
    print("EditSim/BLEU/AST summary (averages):")
    for approach in ["AdaptAgent", "Agentless"]:
        print(f"\n{approach}:")
        for bucket in ["test:Pass", "test:Fail", "manual:Corr", "manual:Incorr"]:
            edit_vals = summary_edit[approach].get(bucket, [])
            bleu_vals = summary_bleu[approach].get(bucket, [])
            ast_vals = summary_ast[approach].get(bucket, [])
            n = len(edit_vals)
            edit_avg = sum(edit_vals) / n if n else 0.0
            bleu_avg = sum(bleu_vals) / n if n else 0.0
            ast_avg = sum(ast_vals) / len(ast_vals) if ast_vals else 0.0
            print(
                f"  {bucket}  n={n}  EditSim={edit_avg:.4f}  BLEU={bleu_avg:.4f}  AST={ast_avg:.4f}"
            )

    if args.out:
        out_payload = {"edit": summary_edit, "bleu": summary_bleu, "ast": summary_ast}
        Path(args.out).write_text(json.dumps(out_payload, indent=2, ensure_ascii=True), encoding="utf-8")

    Path(args.out_adapt).parent.mkdir(parents=True, exist_ok=True)
    Path(args.out_adapt).write_text(json.dumps(adapt_out, indent=2, ensure_ascii=True), encoding="utf-8")
    Path(args.out_agentless).parent.mkdir(parents=True, exist_ok=True)
    Path(args.out_agentless).write_text(json.dumps(agentless_out, indent=2, ensure_ascii=True), encoding="utf-8")

    # Write back similarity scores into the dataset file (in place) using so_key+gh_id.
    dataset_path = Path(args.dataset)
    if dataset_path.exists():
        backup = dataset_path.with_suffix(dataset_path.suffix + ".bak")
        if not backup.exists():
            backup.write_text(dataset_path.read_text(encoding="utf-8"), encoding="utf-8")
        sim_index = {(r.get("so_key"), r.get("gh_id")): r for r in adapt_out}
        updated = dataset
        for rec in updated:
            key = (rec.get("so_key"), rec.get("gh_id"))
            sim = sim_index.get(key)
            if sim:
                rec["edit_sim"] = sim.get("edit_sim")
                rec["bleu_sim"] = sim.get("bleu_sim")
                rec["ast_sim"] = sim.get("ast_sim")
                rec["sim_test_outcome"] = sim.get("test_outcome")
                rec["sim_manual_outcome"] = sim.get("manual_outcome")
        _write_json(dataset_path, updated)


if __name__ == "__main__":
    main()
