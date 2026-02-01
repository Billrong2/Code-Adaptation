from __future__ import annotations

from dataclasses import dataclass
from pathlib import Path
import json
import shutil
import subprocess
import tempfile
from typing import Optional

import paths
import util
from prompt import (
    SO_post_summarization_system_prompt,
    SO_post_summarization_user_prompt,
    guided_self_planning_phase1_system_prompt,
    guided_self_planning_phase1_user_prompt,
    adaptation_system,
    adaptation_user,
)


@dataclass
class VerificationResult:
    ok: bool
    message: str


@dataclass
class AdaptationArtifacts:
    intent_summary: str
    policy_checklist: str
    plan: str
    context_summary: str
    adapted_code: str
    explanation: str
    verification: VerificationResult


class LLMClient:
    def __init__(self, max_retries: int = 3):
        self.max_retries = max_retries

    def complete(self, system_prompt: str, user_prompt: str) -> str:
        return util.llm_gpt5(system_prompt=system_prompt, user_prompt=user_prompt)

    def complete_json(self, system_prompt: str, user_prompt: str) -> dict:
        last_err: Optional[Exception] = None
        for _ in range(self.max_retries):
            try:
                raw = self.complete(system_prompt, user_prompt)
                raw = raw.replace("```json", "").replace("```", "")
                return json.loads(raw)
            except Exception as exc:
                last_err = exc
        raise RuntimeError(f"LLM JSON parse failed after retries: {last_err}")


class IntentSummarizer:
    def __init__(self, llm: LLMClient):
        self.llm = llm

    def summarize(self, full_post: str) -> str:
        user_prompt = SO_post_summarization_user_prompt.replace("<\\SO_POST>", full_post)
        return self.llm.complete(SO_post_summarization_system_prompt, user_prompt).strip()


class PolicyAgent:
    DEFAULT_CATEGORIES = (
        "1) Code Hardening: Enhancing robustness by adding conditionals, handling new "
        "exception types, inserting final modifiers, and managing resources (e.g., closing streams).\n"
        "2) Resolving Compilation Errors: Declaring variables, correcting method calls, removing references "
        "to undefined methods, etc.\n"
        "3) Exception Handling: Modifying try-catch blocks, updating exception types, and refining catch/finally logic.\n"
        "4) Logic Customization: Adjusting method calls, changing constants or variable types, and refining conditionals for adaptation.\n"
        "5) Refactoring: Improving maintainability by renaming, replacing magic values, and restructuring inline fields.\n"
        "6) Other adjustments: Minor modifications, e.g., altering log statements, reformatting code, or updating annotations and comments."
    )

    def build_checklist(self, project_policies: str | None = None) -> str:
        if project_policies:
            return f"{self.DEFAULT_CATEGORIES}\n\nProject-specific policies:\n{project_policies}"
        return self.DEFAULT_CATEGORIES


class ContextMiner:
    def summarize(self, github_codebase: str, file_context: dict | None = None) -> str:
        if file_context:
            return self._format_file_context(file_context)
        try:
            graph = util.call_graph()
            return graph.get_call_graph(github_codebase)
        except Exception:
            return ""

    @staticmethod
    def _format_file_context(file_context: dict) -> str:
        def _join_list(values: list) -> str:
            return ", ".join(values)

        lines = []
        custom_api_imports = file_context.get("custom_api_imports") or []
        if custom_api_imports:
            lines.append(f"custom_api_imports: {_join_list(custom_api_imports)}")

        direct_callers = file_context.get("direct_callers") or []
        if direct_callers:
            lines.append(f"direct_callers: {_join_list(direct_callers)}")

        direct_callees = file_context.get("direct_callees") or []
        if direct_callees:
            lines.append(f"direct_callees: {_join_list(direct_callees)}")

        related_methods = file_context.get("related_methods") or []
        if related_methods:
            lines.append(f"related_methods: {_join_list(related_methods)}")

        call_pairs = file_context.get("call_pairs") or []
        if call_pairs:
            lines.append(f"call_pairs: {_join_list(call_pairs)}")

        caller_callee_map = file_context.get("caller_callee_map") or {}
        if caller_callee_map:
            mapped = []
            for caller, callees in caller_callee_map.items():
                mapped.append(f"{caller} -> [{_join_list(callees)}]")
            lines.append(f"caller_callee_map: {_join_list(mapped)}")

        context_fields = file_context.get("context_fields") or []
        if context_fields:
            lines.append(f"context_fields: {_join_list(context_fields)}")

        context_field_types = file_context.get("context_field_types") or []
        if context_field_types:
            lines.append(f"context_field_types: {_join_list(context_field_types)}")

        max_hops = file_context.get("max_hops")
        if max_hops is not None:
            lines.append(f"max_hops: {max_hops}")

        return "\n".join(lines)


class PlannerAgent:
    def __init__(self, llm: LLMClient):
        self.llm = llm

    def plan(self, so_snippet: str, intent_summary: str, context_summary: str, policy_checklist: str) -> str:
        user_prompt = guided_self_planning_phase1_user_prompt
        user_prompt = user_prompt.replace("<\\code>", so_snippet)
        user_prompt = user_prompt.replace("<repo_level_context>", context_summary or "none")
        user_prompt = user_prompt.replace("<\\SO_POST>", intent_summary)
        user_prompt = user_prompt.replace("<adaptation_catagories>", policy_checklist)
        return self.llm.complete(guided_self_planning_phase1_system_prompt, user_prompt).strip()


class CodeAdapter:
    def __init__(self, llm: LLMClient):
        self.llm = llm

    def adapt(self, intent: str, plan: str, full_post: str, answer_snippet: str,
              github_codebase: str, context_summary: str, verification_errors: str | None) -> tuple[str, str]:
        user_prompt = adaptation_user
        user_prompt = user_prompt.replace("<INTENT>", intent)
        user_prompt = user_prompt.replace("<PLAN>", plan)
        user_prompt = user_prompt.replace("<FULL POST>", full_post)
        user_prompt = user_prompt.replace("<ANSWER>", answer_snippet)
        user_prompt = user_prompt.replace("<CODE BASE>", github_codebase)
        user_prompt = user_prompt.replace("<SIBLING_METHODS_SUMMARY>", context_summary or "none")
        user_prompt = user_prompt.replace("<VERIFICATION_ERRORS>", verification_errors or "none")
        result = self.llm.complete_json(adaptation_system, user_prompt)
        return result["code"], result["explanation"]


class Verifier:
    def __init__(self, javac_path: str | None = None):
        self.javac = javac_path or shutil.which("javac")

    def verify(self, code: str) -> VerificationResult:
        if not code.strip():
            return VerificationResult(False, "empty code")
        if self.javac:
            return self._verify_with_javac(code)
        return self._verify_with_javalang(code)

    def _verify_with_javalang(self, code: str) -> VerificationResult:
        try:
            javalang.parse.parse(f"public class Dummy {{\n{code}\n}}")
            return VerificationResult(True, "parsed")
        except Exception as exc:
            return VerificationResult(False, f"javalang parse error: {exc}")

    def _verify_with_javac(self, code: str) -> VerificationResult:
        with tempfile.TemporaryDirectory() as tmp_dir:
            src = Path(tmp_dir) / "Dummy.java"
            src.write_text(f"public class Dummy {{\n{code}\n}}", encoding="utf-8")
            proc = subprocess.run([self.javac, str(src)], capture_output=True, text=True)
            if proc.returncode == 0:
                return VerificationResult(True, "javac ok")
            msg = (proc.stderr or proc.stdout or "javac failed").strip()
            return VerificationResult(False, msg[:1000])


def _blank_plan() -> str:
    return (
        "code_hardening: none\n"
        "resolve_compilation_errors: none\n"
        "exception_handling: none\n"
        "logic_customization: none\n"
        "refactoring: none\n"
        "misc: none"
    )


class AdaptAgentPipeline:
    def __init__(self, llm: LLMClient, verifier: Verifier | None = None):
        self.summarizer = IntentSummarizer(llm)
        self.policy_agent = PolicyAgent()
        self.context_miner = ContextMiner()
        self.planner = PlannerAgent(llm)
        self.adapter = CodeAdapter(llm)
        self.verifier = verifier

    def run_case(self, case: dict, ablation_intent: bool = False,
                 ablation_context: bool = False, ablation_plan: bool = False,
                 max_verify_rounds: int = 1) -> AdaptationArtifacts:
        full_post = case.get("full_post", "")
        so_snippet = case.get("so_snippet") or case.get("stackoverflow_snippet", "")
        code_base = case.get("file_level_code_without_target") or case.get("github_codebase", "")
        intent_override = (
            case.get("adaptation_intent")
            or case.get("intent_override", "")
        ).strip()
        file_context = case.get("file_context")

        if intent_override:
            intent_summary = intent_override
        elif full_post and not ablation_intent:
            intent_summary = "unknown" if ablation_intent else self.summarizer.summarize(full_post)
        else:
            intent_summary = "unknown"
        policy_checklist = self.policy_agent.build_checklist()
        context_summary = "" if ablation_context else self.context_miner.summarize(
            code_base,
            file_context=file_context,
        )
        plan = _blank_plan() if ablation_plan else self.planner.plan(
            so_snippet, intent_summary, context_summary, policy_checklist
        )

        verification = VerificationResult(True, "not run")
        adapted_code = ""
        explanation = ""
        verification_errors = None

        for _ in range(max_verify_rounds):
            adapted_code, explanation = self.adapter.adapt(
                intent_summary,
                plan,
                full_post,
                so_snippet,
                code_base,
                context_summary,
                verification_errors,
            )
            if not self.verifier:
                break
            verification = self.verifier.verify(adapted_code)
            if verification.ok:
                break
            verification_errors = verification.message

        return AdaptationArtifacts(
            intent_summary=intent_summary,
            policy_checklist=policy_checklist,
            plan=plan,
            context_summary=context_summary,
            adapted_code=adapted_code,
            explanation=explanation,
            verification=verification,
        )


def write_case_outputs(case_id: str, artifacts: AdaptationArtifacts, developer_edit: str | None = None) -> None:
    paths.ensure_results_dirs()
    case_root = paths.case_root_dir(case_id)
    case_root.mkdir(parents=True, exist_ok=True)
    (case_root / "adapted_code.txt").write_text(artifacts.adapted_code, encoding="utf-8")
    (case_root / "explanation.txt").write_text(artifacts.explanation, encoding="utf-8")
    (case_root / "intent_summary.txt").write_text(artifacts.intent_summary, encoding="utf-8")
    (case_root / "plan.txt").write_text(artifacts.plan, encoding="utf-8")
    (case_root / "context_summary.txt").write_text(artifacts.context_summary, encoding="utf-8")
    (case_root / "verification.txt").write_text(
        f"{artifacts.verification.ok}\n{artifacts.verification.message}",
        encoding="utf-8",
    )
    if developer_edit is not None:
        (case_root / "developer_edit.txt").write_text(developer_edit, encoding="utf-8")
