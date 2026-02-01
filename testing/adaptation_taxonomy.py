#!/usr/bin/env python3
from __future__ import annotations

import argparse
import json
import os
import re
import subprocess
import tempfile
from dataclasses import dataclass
from pathlib import Path
from typing import Any, Dict, Iterable, List, Optional, Sequence, Set, Tuple

import javalang
from tqdm import tqdm

# Main categories (faithful, coarse-grained)
CAT_CODE_HARDENING = "Code Hardening"
CAT_RESOLVE_COMPILATION = "Resolve Compilation Errors"
CAT_EXCEPTION_HANDLING = "Exception Handling"
CAT_LOGIC_CUSTOMIZATION = "Logic Customization"
CAT_REFACTORING = "Refactoring"
CAT_MISC = "Miscellaneous"

MAIN_CATEGORIES = {
    CAT_CODE_HARDENING,
    CAT_RESOLVE_COMPILATION,
    CAT_EXCEPTION_HANDLING,
    CAT_LOGIC_CUSTOMIZATION,
    CAT_REFACTORING,
    CAT_MISC,
}

CLEANUP_METHODS = {"close", "dispose", "recycle", "shutdown"}
LOG_METHODS = {"print", "println", "printf", "log", "logger", "info", "debug", "warn", "error"}


@dataclass
class GTNode:
    type: str
    label: str = ""
    parent_type: str = ""


def _wrap_java(snippet: str) -> str:
    s = snippet.strip()
    if not s:
        return "class Dummy {}"
    if "class " in s or s.lstrip().startswith("public class"):
        return s
    return f"public class Dummy {{\n{s}\n}}"


def _run_gumtree(gumtree: str, left: Path, right: Path) -> Dict[str, Any]:
    cmd = [gumtree, "diff", "-f", "json", str(left), str(right)]
    proc = subprocess.run(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
    if proc.returncode != 0:
        raise RuntimeError(proc.stderr.strip() or "gumtree diff failed")
    return json.loads(proc.stdout)


def _extract_actions(payload: Dict[str, Any]) -> List[Tuple[str, GTNode]]:
    actions: List[Tuple[str, GTNode]] = []
    for act in payload.get("actions", []):
        action = act.get("action", "").lower()
        node = act.get("node", {}) or {}
        parent = act.get("parent", {}) or {}
        actions.append(
            (
                action,
                GTNode(
                    type=node.get("type", ""),
                    label=node.get("label", "") or node.get("value", "") or "",
                    parent_type=parent.get("type", ""),
                ),
            )
        )
    return actions


def _parse_java(code: str) -> Optional[javalang.tree.CompilationUnit]:
    try:
        return javalang.parse.parse(code)
    except Exception:
        return None


@dataclass
class Semantics:
    declared_vars: Set[str]
    used_vars: Set[str]
    method_calls: List[Tuple[str, Optional[str]]]
    exception_types: Set[str]
    annotations: Set[str]
    modifiers: Set[str]
    literals: Set[str]


def _collect_semantics(tree: javalang.tree.CompilationUnit) -> Semantics:
    declared_vars: Set[str] = set()
    used_vars: Set[str] = set()
    method_calls: List[Tuple[str, Optional[str]]] = []
    exception_types: Set[str] = set()
    annotations: Set[str] = set()
    modifiers: Set[str] = set()
    literals: Set[str] = set()

    for _, node in tree:
        if isinstance(node, javalang.tree.VariableDeclarator):
            declared_vars.add(node.name)
        if isinstance(node, javalang.tree.MemberReference):
            if node.member:
                used_vars.add(node.member)
        if isinstance(node, javalang.tree.MethodInvocation):
            method_calls.append((node.member, node.qualifier))
        if isinstance(node, javalang.tree.SuperMethodInvocation):
            method_calls.append((node.member, "super"))
        if isinstance(node, javalang.tree.ClassCreator):
            # treat constructor as a call for some heuristics
            if node.type and getattr(node.type, "name", None):
                method_calls.append((node.type.name, "new"))
        if isinstance(node, javalang.tree.CatchClauseParameter):
            if node.types:
                for t in node.types:
                    if hasattr(t, "name"):
                        exception_types.add(t.name)
        if isinstance(node, javalang.tree.MethodDeclaration):
            if node.throws:
                for t in node.throws:
                    if hasattr(t, "name"):
                        exception_types.add(t.name)
        if isinstance(node, javalang.tree.Annotation):
            if getattr(node, "name", None):
                annotations.add(node.name)
        # Modifiers are stored as a set of strings on many node types (methods, fields, classes)
        if hasattr(node, "modifiers") and isinstance(node.modifiers, (set, list, tuple)):
            for m in node.modifiers:
                if isinstance(m, str):
                    modifiers.add(m)
        if isinstance(node, javalang.tree.Literal):
            if node.value is not None:
                literals.add(node.value)

    return Semantics(
        declared_vars=declared_vars,
        used_vars=used_vars,
        method_calls=method_calls,
        exception_types=exception_types,
        annotations=annotations,
        modifiers=modifiers,
        literals=literals,
    )


def _find_joern(joern_root: str) -> Tuple[Optional[Path], Optional[Path]]:
    root = Path(joern_root)
    if root.is_file() and os.access(str(root), os.X_OK):
        joern = root
        joern_parse = root.parent / "joern-parse"
    else:
        joern = root / "joern"
        joern_parse = root / "joern-parse"
    if joern.exists() and os.access(str(joern), os.X_OK) and joern_parse.exists() and os.access(str(joern_parse), os.X_OK):
        return joern, joern_parse
    return None, None


def _collect_semantics_joern(code: str, joern_root: str) -> Optional[Semantics]:
    """
    Best-effort semantics extraction using Joern (if available).
    Falls back to None on any failure.
    """
    joern, joern_parse = _find_joern(joern_root)
    if not joern or not joern_parse:
        return None

    wrapped = _wrap_java(code)
    try:
        with tempfile.TemporaryDirectory() as tmpdir:
            tmp_path = Path(tmpdir)
            src_dir = tmp_path / "src"
            src_dir.mkdir(parents=True, exist_ok=True)
            (src_dir / "Snippet.java").write_text(wrapped, encoding="utf-8")

            cpg_path = tmp_path / "cpg.bin"
            parse_cmd = [str(joern_parse), str(src_dir), "--output", str(cpg_path)]
            proc = subprocess.run(parse_cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
            if proc.returncode != 0 or not cpg_path.exists():
                return None

            # Joern script to extract basic semantics (calls + exceptions + annotations)
            script = tmp_path / "query.sc"
            script.write_text(
                "\n".join(
                    [
                        "import io.shiftleft.codepropertygraph.CpgLoader",
                        "import io.shiftleft.semanticcpg.language._",
                        "val cpg = CpgLoader.load(cpgFile)",
                        "val calls = cpg.call.map(c => s\"${c.name}\\t${c.dispatchType}\").l",
                        "val excs = cpg.call.name(\"<operator>.throw\").argument.typeFullName.l",
                        "val anns = cpg.annotation.name.l",
                        "val lits = cpg.literal.code.l",
                        "println(\"CALLS=\" + calls.mkString(\"||\"))",
                        "println(\"EXCS=\" + excs.distinct.mkString(\"||\"))",
                        "println(\"ANNS=\" + anns.distinct.mkString(\"||\"))",
                        "println(\"LITS=\" + lits.distinct.mkString(\"||\"))",
                    ]
                ),
                encoding="utf-8",
            )

            query_cmd = [str(joern), "--script", str(script), "--params", f"cpgFile={cpg_path}"]
            qproc = subprocess.run(query_cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
            if qproc.returncode != 0:
                return None

            calls_line = ""
            excs_line = ""
            anns_line = ""
            lits_line = ""
            for line in qproc.stdout.splitlines():
                if line.startswith("CALLS="):
                    calls_line = line[len("CALLS=") :]
                elif line.startswith("EXCS="):
                    excs_line = line[len("EXCS=") :]
                elif line.startswith("ANNS="):
                    anns_line = line[len("ANNS=") :]
                elif line.startswith("LITS="):
                    lits_line = line[len("LITS=") :]

            method_calls: List[Tuple[str, Optional[str]]] = []
            if calls_line:
                for item in calls_line.split("||"):
                    if not item:
                        continue
                    name, dispatch = (item.split("\t") + [""])[:2]
                    qualifier = None if dispatch == "STATIC_DISPATCH" else "instance"
                    method_calls.append((name, qualifier))

            exception_types = set(filter(None, excs_line.split("||"))) if excs_line else set()
            annotations = set(filter(None, anns_line.split("||"))) if anns_line else set()
            literals = set(filter(None, lits_line.split("||"))) if lits_line else set()

            # Joern doesn't provide def/use here; leave empty (javalang will fill when used)
            return Semantics(
                declared_vars=set(),
                used_vars=set(),
                method_calls=method_calls,
                exception_types=exception_types,
                annotations=annotations,
                modifiers=set(),
                literals=literals,
            )
    except Exception:
        return None


def _method_call_delta(old_calls: List[Tuple[str, Optional[str]]], new_calls: List[Tuple[str, Optional[str]]]) -> bool:
    """
    Heuristic: detect qualifier added to an existing method call (local -> instance).
    """
    old_map: Dict[str, Set[Optional[str]]] = {}
    for name, qual in old_calls:
        old_map.setdefault(name, set()).add(qual)
    for name, qual in new_calls:
        if name in old_map:
            if None in old_map[name] and qual not in (None, ""):
                return True
    return False


def _categorize(
    actions: List[Tuple[str, GTNode]],
    old_sem: Optional[Semantics],
    new_sem: Optional[Semantics],
) -> Set[str]:
    cats: Set[str] = set()

    def is_log_method(name: str) -> bool:
        return name.lower() in LOG_METHODS

    # Action-driven (syntactic) rules
    for action, node in actions:
        ntype = node.type
        label = node.label
        parent = node.parent_type

        # Code Hardening
        if action == "insert" and ntype == "IfStatement":
            cats.add(CAT_CODE_HARDENING)
        if action == "insert" and ntype == "Modifier" and label == "final":
            cats.add(CAT_CODE_HARDENING)
        if action == "insert" and ntype in {"MethodInvocation", "SuperMethodInvocation"} and label in CLEANUP_METHODS:
            cats.add(CAT_CODE_HARDENING)

        # Resolve Compilation Errors
        if action == "insert" and ntype in {"VariableDeclaration", "VariableDeclarationStatement", "FieldDeclaration"}:
            cats.add(CAT_RESOLVE_COMPILATION)
        if action == "delete" and ntype in {"Name", "SimpleName"} and parent in {"MethodInvocation", "SimpleName"}:
            cats.add(CAT_RESOLVE_COMPILATION)

        # Exception Handling
        if action in {"insert", "delete"} and ntype == "TryStatement":
            cats.add(CAT_EXCEPTION_HANDLING)
        if action == "update" and ntype in {"CatchClause", "MethodDeclaration"}:
            cats.add(CAT_EXCEPTION_HANDLING)
        if action == "update" and ntype in {"SimpleType", "QualifiedType"} and parent in {"CatchClause", "MethodDeclaration"}:
            cats.add(CAT_EXCEPTION_HANDLING)

        # Logic Customization
        if action == "update" and ntype == "Literal":
            cats.add(CAT_LOGIC_CUSTOMIZATION)
        if action == "update" and ntype in {"InfixExpression", "ConditionalExpression", "PrefixExpression"}:
            cats.add(CAT_LOGIC_CUSTOMIZATION)
        if action == "update" and ntype in {"MethodInvocation", "SuperMethodInvocation"}:
            cats.add(CAT_LOGIC_CUSTOMIZATION)
        if action == "update" and ntype in {"BasicType", "ReferenceType", "ClassOrInterfaceType"}:
            cats.add(CAT_LOGIC_CUSTOMIZATION)

        # Refactoring
        if action == "update" and ntype in {"SimpleName", "QualifiedName"}:
            cats.add(CAT_REFACTORING)
        if action == "update" and ntype == "Modifier" and label in {"public", "protected", "private", "static"}:
            cats.add(CAT_REFACTORING)

        # Miscellaneous
        if action in {"insert", "update", "delete"} and ntype == "Annotation":
            cats.add(CAT_MISC)
        if action in {"insert", "update", "delete"} and ntype == "Comment":
            cats.add(CAT_MISC)
        if action == "update" and ntype in {"MethodInvocation", "SuperMethodInvocation"} and is_log_method(label):
            cats.add(CAT_MISC)

    # Semantic-ish heuristics (javalang)
    if old_sem and new_sem:
        # Resolve compilation errors: declared vars added that were previously used but undeclared.
        if (new_sem.declared_vars - old_sem.declared_vars) & old_sem.used_vars:
            cats.add(CAT_RESOLVE_COMPILATION)
        # Resolve compilation errors: method call qualified
        if _method_call_delta(old_sem.method_calls, new_sem.method_calls):
            cats.add(CAT_RESOLVE_COMPILATION)
        # Exception Handling: exception types changed
        if old_sem.exception_types != new_sem.exception_types:
            cats.add(CAT_EXCEPTION_HANDLING)
        # Logic customization: literal set changed
        if old_sem.literals != new_sem.literals:
            cats.add(CAT_LOGIC_CUSTOMIZATION)
        # Misc: annotations changed
        if old_sem.annotations != new_sem.annotations:
            cats.add(CAT_MISC)
        # Refactoring: modifiers/access changed
        if old_sem.modifiers != new_sem.modifiers:
            cats.add(CAT_REFACTORING)

    return cats & MAIN_CATEGORIES


def _classify_pair(
    gh_snippet: str,
    adapted: str,
    gumtree_bin: str,
    joern_root: Optional[str],
) -> Tuple[Set[str], str]:
    left_code = _wrap_java(gh_snippet)
    right_code = _wrap_java(adapted)

    left_tree = _parse_java(left_code)
    right_tree = _parse_java(right_code)
    old_sem = _collect_semantics(left_tree) if left_tree else None
    new_sem = _collect_semantics(right_tree) if right_tree else None

    # Optional Joern enrichment (if available) for call/exception/literal semantics
    if joern_root:
        old_j = _collect_semantics_joern(left_code, joern_root)
        new_j = _collect_semantics_joern(right_code, joern_root)
        if old_j:
            old_sem = Semantics(
                declared_vars=old_sem.declared_vars if old_sem else set(),
                used_vars=old_sem.used_vars if old_sem else set(),
                method_calls=old_j.method_calls or (old_sem.method_calls if old_sem else []),
                exception_types=old_j.exception_types or (old_sem.exception_types if old_sem else set()),
                annotations=old_j.annotations or (old_sem.annotations if old_sem else set()),
                modifiers=old_sem.modifiers if old_sem else set(),
                literals=old_j.literals or (old_sem.literals if old_sem else set()),
            )
        if new_j:
            new_sem = Semantics(
                declared_vars=new_sem.declared_vars if new_sem else set(),
                used_vars=new_sem.used_vars if new_sem else set(),
                method_calls=new_j.method_calls or (new_sem.method_calls if new_sem else []),
                exception_types=new_j.exception_types or (new_sem.exception_types if new_sem else set()),
                annotations=new_j.annotations or (new_sem.annotations if new_sem else set()),
                modifiers=new_sem.modifiers if new_sem else set(),
                literals=new_j.literals or (new_sem.literals if new_sem else set()),
            )

    with tempfile.TemporaryDirectory() as tmpdir:
        left_path = Path(tmpdir) / "left.java"
        right_path = Path(tmpdir) / "right.java"
        left_path.write_text(left_code, encoding="utf-8")
        right_path.write_text(right_code, encoding="utf-8")
        payload = _run_gumtree(gumtree_bin, left_path, right_path)
        actions = _extract_actions(payload)
        cats = _categorize(actions, old_sem, new_sem)
    return cats, "ok"


def _classify_pair_semantic_only(gh_snippet: str, adapted: str, joern_root: Optional[str]) -> Set[str]:
    left_code = _wrap_java(gh_snippet)
    right_code = _wrap_java(adapted)
    left_tree = _parse_java(left_code)
    right_tree = _parse_java(right_code)
    old_sem = _collect_semantics(left_tree) if left_tree else None
    new_sem = _collect_semantics(right_tree) if right_tree else None
    if joern_root:
        old_j = _collect_semantics_joern(left_code, joern_root)
        new_j = _collect_semantics_joern(right_code, joern_root)
        if old_j and old_sem:
            old_sem.method_calls = old_j.method_calls or old_sem.method_calls
            old_sem.exception_types = old_j.exception_types or old_sem.exception_types
            old_sem.annotations = old_j.annotations or old_sem.annotations
            old_sem.literals = old_j.literals or old_sem.literals
        if new_j and new_sem:
            new_sem.method_calls = new_j.method_calls or new_sem.method_calls
            new_sem.exception_types = new_j.exception_types or new_sem.exception_types
            new_sem.annotations = new_j.annotations or new_sem.annotations
            new_sem.literals = new_j.literals or new_sem.literals
    # No GumTree actions, just semantic diffs + lightweight regex.
    cats = _categorize([], old_sem, new_sem)
    # Lightweight syntactic checks for the self-check use case
    if re.search(r"\bif\s*\(", adapted) and not re.search(r"\bif\s*\(", gh_snippet):
        cats.add(CAT_CODE_HARDENING)
    if re.search(r"\btry\s*\{", adapted) and not re.search(r"\btry\s*\{", gh_snippet):
        cats.add(CAT_EXCEPTION_HANDLING)
    return cats & MAIN_CATEGORIES


def main() -> None:
    parser = argparse.ArgumentParser(description="Classify adaptations into 6 main categories using GumTree + javalang.")
    parser.add_argument("--dataset", required=True, help="Dataset JSON path.")
    parser.add_argument("--output", required=True, help="Output JSON path.")
    parser.add_argument("--gumtree", default=os.environ.get("GUMTREE", "gumtree"), help="Path to gumtree CLI.")
    parser.add_argument("--joern-root", default=os.environ.get("JOERN_CLI", "/bin/joern/joern-cli"), help="Path to joern CLI root.")
    parser.add_argument("--use-joern", action="store_true", help="Enable Joern enrichment if available.")
    parser.add_argument("--limit", type=int, default=0, help="Limit number of records (0 = all).")
    parser.add_argument("--self-check", action="store_true", help="Run two tiny examples and exit.")
    args = parser.parse_args()

    if args.self_check:
        examples = [
            {
                "name": "code_hardening_if",
                "gh": "int f(int x){return x;}",
                "adapted": "int f(int x){ if(x>0){return x;} return 0; }",
            },
            {
                "name": "logic_customization_literal",
                "gh": "int f(){ return 1; }",
                "adapted": "int f(){ return 2; }",
            },
        ]
        for ex in examples:
            try:
                cats, status = _classify_pair(
                    ex["gh"], ex["adapted"], args.gumtree, args.joern_root if args.use_joern else None
                )
                print(f"{ex['name']}: {sorted(cats)} (status={status})")
            except Exception as exc:
                cats = _classify_pair_semantic_only(
                    ex["gh"], ex["adapted"], args.joern_root if args.use_joern else None
                )
                print(f"{ex['name']}: {sorted(cats)} (status=gumtree_unavailable: {exc})")
        return

    dataset_path = Path(args.dataset)
    output_path = Path(args.output)
    data = json.loads(dataset_path.read_text(encoding="utf-8"))

    total = min(args.limit, len(data)) if args.limit else len(data)
    iterable = data[: args.limit] if args.limit else data

    results: List[Dict[str, Any]] = []
    for record in tqdm(iterable, total=total, desc="adaptation-taxonomy"):
        gh_snippet = record.get("gh_snippet", "")
        adapted = record.get("adapt_agent_code") or ""
        row = {
            "so_key": record.get("so_key"),
            "gh_id": record.get("gh_id"),
            "status": "ok",
            "categories": [],
        }
        if not gh_snippet or not adapted:
            row["status"] = "missing_input"
            results.append(row)
            continue
        try:
            cats, status = _classify_pair(gh_snippet, adapted, args.gumtree, args.joern_root if args.use_joern else None)
            row["categories"] = sorted(cats)
            row["status"] = status
        except Exception as exc:
            row["status"] = "error"
            row["error"] = str(exc)
        results.append(row)

    output_path.parent.mkdir(parents=True, exist_ok=True)
    output_path.write_text(json.dumps(results, indent=2, ensure_ascii=True), encoding="utf-8")


if __name__ == "__main__":
    main()
