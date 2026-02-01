#!/usr/bin/env python3
from __future__ import annotations

import argparse
import json
import os
import subprocess
import tempfile
from dataclasses import dataclass
import re
from pathlib import Path
from typing import Any, Dict, List, Optional, Set, Tuple, Iterable
from collections import Counter

from tqdm import tqdm


@dataclass
class GTNode:
    type: str
    label: str = ""
    parent_type: str = ""
    start: int = -1
    end: int = -1


@dataclass
class TreeNode:
    type: str
    label: str
    start: int
    end: int
    parent: Optional["TreeNode"] = None
    children: List["TreeNode"] = None

    def __post_init__(self) -> None:
        if self.children is None:
            self.children = []

    @property
    def key(self) -> Tuple[str, str, int, int]:
        return (self.type, self.label, self.start, self.end)

    def ancestors(self) -> Iterable["TreeNode"]:
        cur = self.parent
        while cur is not None:
            yield cur
            cur = cur.parent

    def has_ancestor_type(self, t: str) -> bool:
        return any(a.type == t for a in self.ancestors())


def _wrap_java(snippet: str) -> str:
    s = snippet.strip()
    if not s:
        return "class Dummy {}"
    type_decl_re = re.compile(
        r"^\s*(?:public\s+)?(?:protected\s+)?(?:private\s+)?(?:abstract\s+)?(?:final\s+)?(?:class|interface|enum)\s+\w+",
        flags=re.M,
    )

    def extract_type_body(src: str) -> str:
        # Extract the body of the first top-level type declaration and return it.
        # This avoids mismatches when one side includes `class Foo { ... }` while the other is just a method.
        start = src.find("{")
        if start < 0:
            return src
        level = 0
        end = -1
        for i in range(start, len(src)):
            ch = src[i]
            if ch == "{":
                level += 1
            elif ch == "}":
                level -= 1
                if level == 0:
                    end = i
                    break
        if end < 0 or end <= start:
            return src
        body = src[start + 1 : end].strip()
        return body if body else src

    # Detect real type declarations, not ".class" literals (e.g., "String.class").
    # We match at start-of-line to avoid false positives like "String.class ".
    if type_decl_re.search(s):
        inner = extract_type_body(s)
        return f"public class Dummy {{\n{inner}\n}}"

    return f"public class Dummy {{\n{s}\n}}"


def _default_gumtree_bin() -> str:
    """
    Try to locate a GumTree CLI installed under HOME first (root FS may be full),
    then fall back to 'gumtree' (PATH).
    """
    env = os.environ.get("GUMTREE")
    if env:
        return env
    # Common local install path we create in this project.
    home = Path.home()
    candidates = list((home / "tools" / "gumtree").glob("gumtree-*/bin/gumtree"))
    if candidates:
        return str(sorted(candidates)[-1])
    return "gumtree"


def _parse_tree_repr(tree: str) -> Tuple[str, str]:
    """
    GumTree JSON (v3+) encodes nodes as strings like:
      - 'IfStatement [19,37]'
      - 'BooleanLiteral: true [22,26]'
    Return (type, label).
    """
    s = (tree or "").strip()
    if not s:
        return "", ""
    # Strip location suffix
    s = s.split(" [", 1)[0]
    if ":" in s:
        t, lbl = s.split(":", 1)
        return t.strip(), lbl.strip()
    return s.strip(), ""


def _run_gumtree(gumtree: str, left: Path, right: Path) -> Dict[str, Any]:
    # GumTree v3 uses 'textdiff' with -f JSON.
    cmd = [gumtree, "textdiff", "-f", "JSON", str(left), str(right)]
    proc = subprocess.run(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
    # NOTE: GumTree can sometimes return 0 while writing errors to stderr and no JSON to stdout.
    if proc.returncode != 0 or proc.stderr.strip() or not proc.stdout.strip():
        msg = proc.stderr.strip() or proc.stdout.strip() or "gumtree diff failed"
        raise RuntimeError(msg)
    return json.loads(proc.stdout)


_NODE_RE = re.compile(r"^\s*([A-Za-z0-9_]+)(?::\s*(.*?))?\s*\[(\d+),(\d+)\]\s*$")


def _parse_node_line(line: str) -> Tuple[str, str, int, int]:
    """
    Parse GumTree node lines such as:
      - 'MethodInvocation [31,37]'
      - 'SimpleName: foo [31,34]'
    Returns (type, label, start, end).
    """
    m = _NODE_RE.match(line.strip())
    if not m:
        raise ValueError(f"Unparseable node line: {line!r}")
    t = m.group(1)
    lbl = (m.group(2) or "").strip()
    start = int(m.group(3))
    end = int(m.group(4))
    return t, lbl, start, end


def _run_parse_tree(gumtree: str, path: Path) -> str:
    cmd = [gumtree, "parse", str(path)]
    proc = subprocess.run(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
    if proc.returncode != 0 or proc.stderr.strip() or not proc.stdout.strip():
        msg = proc.stderr.strip() or proc.stdout.strip() or "gumtree parse failed"
        raise RuntimeError(msg)
    return proc.stdout


def _build_tree(parse_output: str) -> Tuple[TreeNode, Dict[Tuple[str, str, int, int], List[TreeNode]]]:
    """
    Build a parent/children tree from `gumtree parse` output.
    Returns (root, index) where index maps node key -> list of nodes.
    """
    lines = [ln.rstrip("\n") for ln in (parse_output or "").splitlines() if ln.strip()]
    if not lines:
        raise RuntimeError("empty parse tree output")
    stack: List[Tuple[int, TreeNode]] = []
    index: Dict[Tuple[str, str, int, int], List[TreeNode]] = {}
    root: Optional[TreeNode] = None

    for ln in lines:
        indent = len(ln) - len(ln.lstrip(" "))
        level = indent // 4
        t, lbl, start, end = _parse_node_line(ln)
        node = TreeNode(type=t, label=lbl, start=start, end=end)
        index.setdefault(node.key, []).append(node)
        if level == 0:
            root = node
            stack = [(level, node)]
            continue
        while stack and stack[-1][0] >= level:
            stack.pop()
        if not stack:
            # malformed indentation; treat as root
            root = node
            stack = [(level, node)]
            continue
        parent = stack[-1][1]
        node.parent = parent
        parent.children.append(node)
        stack.append((level, node))

    if root is None:
        raise RuntimeError("failed to build tree (no root)")
    return root, index


@dataclass
class DiffOp:
    kind: str  # match/insert/delete/update/move
    left: Optional[Tuple[str, str, int, int]] = None
    right: Optional[Tuple[str, str, int, int]] = None
    parent: Optional[Tuple[str, str, int, int]] = None
    at: Optional[int] = None


def _run_textdiff(gumtree: str, left: Path, right: Path) -> str:
    cmd = [gumtree, "textdiff", str(left), str(right)]
    proc = subprocess.run(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
    if proc.returncode != 0 or proc.stderr.strip() or not proc.stdout.strip():
        msg = proc.stderr.strip() or proc.stdout.strip() or "gumtree textdiff failed"
        raise RuntimeError(msg)
    return proc.stdout


def _parse_textdiff(text: str) -> Tuple[List[Tuple[Tuple[str, str, int, int], Tuple[str, str, int, int]]], List[DiffOp]]:
    """
    Parse `gumtree textdiff left right` output.
    Returns (matches, ops).
    """
    lines = [ln.rstrip("\n") for ln in (text or "").splitlines()]
    blocks: List[Tuple[str, List[str]]] = []
    i = 0
    while i < len(lines):
        if lines[i].strip() != "===":
            i += 1
            continue
        if i + 2 >= len(lines):
            break
        btype = lines[i + 1].strip()
        # expect '---' at i+2
        i += 3
        content: List[str] = []
        while i < len(lines) and lines[i].strip() != "===":
            content.append(lines[i])
            i += 1
        blocks.append((btype, content))

    matches: List[Tuple[Tuple[str, str, int, int], Tuple[str, str, int, int]]] = []
    ops: List[DiffOp] = []

    for btype, content in blocks:
        bt = btype.strip()
        # normalize gumtree op names
        if bt == "match":
            node_lines = [c.strip() for c in content if c.strip()]
            if len(node_lines) >= 2:
                l = _parse_node_line(node_lines[0])
                r = _parse_node_line(node_lines[1])
                matches.append(((l[0], l[1], l[2], l[3]), (r[0], r[1], r[2], r[3])))
            continue

        if bt.startswith("update"):
            # update-node block: first non-empty line is the left node
            node_lines = [c.strip() for c in content if c.strip()]
            if not node_lines:
                continue
            l = _parse_node_line(node_lines[0])
            ops.append(DiffOp(kind="update", left=(l[0], l[1], l[2], l[3])))
            continue

        if bt.startswith("delete"):
            node_lines = [c.strip() for c in content if c.strip()]
            if not node_lines:
                continue
            l = _parse_node_line(node_lines[0])
            ops.append(DiffOp(kind="delete", left=(l[0], l[1], l[2], l[3])))
            continue

        if bt.startswith("insert"):
            # insert-node/tree:
            # <node>
            # to
            # <parent>
            # at <i>
            node_lines = [c.rstrip() for c in content if c.strip()]
            if not node_lines:
                continue
            node = _parse_node_line(node_lines[0])
            parent = None
            at = None
            for j, ln in enumerate(node_lines):
                if ln.strip() == "to" and j + 1 < len(node_lines):
                    parent = _parse_node_line(node_lines[j + 1].strip())
                if ln.strip().startswith("at"):
                    try:
                        at = int(ln.strip().split(" ", 1)[1])
                    except Exception:
                        at = None
            ops.append(
                DiffOp(
                    kind="insert",
                    right=(node[0], node[1], node[2], node[3]),
                    parent=(parent[0], parent[1], parent[2], parent[3]) if parent else None,
                    at=at,
                )
            )
            continue

        if bt.startswith("move"):
            # move-tree:
            # <tree root + possibly subtree lines>
            # to
            # <parent>
            # at <i>
            node_lines = [c.rstrip() for c in content if c.strip()]
            if not node_lines:
                continue
            # first line is root of moved subtree
            moved = _parse_node_line(node_lines[0].strip())
            parent = None
            at = None
            for j, ln in enumerate(node_lines):
                if ln.strip() == "to" and j + 1 < len(node_lines):
                    parent = _parse_node_line(node_lines[j + 1].strip())
                if ln.strip().startswith("at"):
                    try:
                        at = int(ln.strip().split(" ", 1)[1])
                    except Exception:
                        at = None
            ops.append(
                DiffOp(
                    kind="move",
                    left=(moved[0], moved[1], moved[2], moved[3]),
                    parent=(parent[0], parent[1], parent[2], parent[3]) if parent else None,
                    at=at,
                )
            )
            continue

    return matches, ops


def _pick_one(index: Dict[Tuple[str, str, int, int], List[TreeNode]], key: Tuple[str, str, int, int]) -> Optional[TreeNode]:
    lst = index.get(key)
    if not lst:
        return None
    return lst[0]


def _descendants(node: TreeNode) -> Iterable[TreeNode]:
    for c in node.children:
        yield c
        yield from _descendants(c)


def _find_finally_block_nodes(root: TreeNode) -> Set[Tuple[str, str, int, int]]:
    """
    GumTree/JDT represents finally blocks as a plain `Block` child of `TryStatement`
    that comes after any `CatchClause` children.
    Return a set of keys for that finally-block subtree (block + descendants).
    """
    keys: Set[Tuple[str, str, int, int]] = set()
    stack = [root]
    while stack:
        n = stack.pop()
        if n.type == "TryStatement":
            # children order: try Block, CatchClause*, finally Block?
            block_children = [c for c in n.children if c.type == "Block"]
            catch_children = [c for c in n.children if c.type == "CatchClause"]
            if block_children and len(block_children) >= 2:
                # heuristically: last Block is finally when there is at least one catch
                if catch_children:
                    finally_block = block_children[-1]
                    keys.add(finally_block.key)
                    for d in _descendants(finally_block):
                        keys.add(d.key)
        stack.extend(n.children)
    return keys


def _analyze_semantics(root: TreeNode) -> Dict[str, Any]:
    """
    Collect lightweight semantic-ish facts from the GumTree/JDT AST.
    This is a best-effort implementation for the taxonomy predicates.
    """
    defs: Set[str] = set()
    uses: Set[str] = set()
    local_calls: Set[str] = set()
    instance_calls: Set[str] = set()
    exceptions: Set[str] = set()
    thrown: Set[str] = set()

    # helpers
    def is_type_context(node: TreeNode) -> bool:
        return node.parent is not None and node.parent.type in {"SimpleType", "QualifiedType"}

    def is_def_context(node: TreeNode) -> bool:
        return node.parent is not None and node.parent.type in {"VariableDeclarationFragment", "SingleVariableDeclaration"}

    def is_method_name_context(node: TreeNode) -> bool:
        return node.parent is not None and node.parent.type == "MethodInvocation"

    # Walk tree
    stack = [root]
    while stack:
        n = stack.pop()
        stack.extend(n.children)

        if n.type == "VariableDeclarationFragment":
            for c in n.children:
                if c.type == "SimpleName" and c.label:
                    defs.add(c.label)
                    break
        if n.type == "SingleVariableDeclaration":
            for c in n.children:
                if c.type == "SimpleName" and c.label:
                    defs.add(c.label)
        if n.type == "MethodInvocation":
            recv = any(c.type == "METHOD_INVOCATION_RECEIVER" for c in n.children)
            name = ""
            for c in n.children:
                if c.type == "SimpleName" and c.label:
                    name = c.label
                    break
            if name:
                if recv:
                    instance_calls.add(name)
                else:
                    local_calls.add(name)
        if n.type == "CatchClause":
            # CatchClause -> SingleVariableDeclaration -> SimpleType -> SimpleName
            for d in _descendants(n):
                if d.type == "SimpleName" and d.label and d.parent and d.parent.type in {"SimpleType", "QualifiedType"}:
                    exceptions.add(d.label)
                    break
        if n.type == "SimpleName" and n.label:
            if is_def_context(n) or is_type_context(n) or is_method_name_context(n):
                continue
            uses.add(n.label)

    # Thrown exceptions: SimpleType under MethodDeclaration with "throws" list isn't explicit in this tree dump.
    # Best-effort: treat any SimpleName under SimpleType where the SimpleType is directly under MethodDeclaration
    # and looks like an exception as "thrown".
    stack = [root]
    while stack:
        n = stack.pop()
        stack.extend(n.children)
        if n.type == "MethodDeclaration":
            for c in _descendants(n):
                if c.type == "SimpleName" and c.label and c.parent and c.parent.type in {"SimpleType", "QualifiedType"}:
                    if c.label.endswith("Exception") or c.label in {"Throwable", "Error", "Exception"}:
                        thrown.add(c.label)
    return {
        "defs": defs,
        "uses": uses,
        "local_calls": local_calls,
        "instance_calls": instance_calls,
        "exceptions": exceptions,
        "thrown": thrown,
    }


def _is_exception_type(name: str) -> bool:
    return bool(name) and (name.endswith("Exception") or name in {"Throwable", "Error", "Exception"})


def _is_clean_method(name: str) -> bool:
    return name.lower() in {"close", "dispose", "recycle"}


def _is_log_method(name: str) -> bool:
    return name.lower() in {"print", "println", "printf", "log", "logger", "info", "debug", "warn", "error"}


def _main_category_from_type(t: str) -> str:
    if t.startswith("Code Hardening"):
        return "Code Hardening"
    if t.startswith("Resolve Compilation Errors"):
        return "Resolve Compilation Errors"
    if t.startswith("Exception Handling"):
        return "Exception Handling"
    if t.startswith("Logic Customization"):
        return "Logic Customization"
    if t.startswith("Refactoring"):
        return "Refactoring"
    if t.startswith("Miscellaneous"):
        return "Miscellaneous"
    # already a main label
    return t


def _categorize_exact(
    left_root: TreeNode,
    right_root: TreeNode,
    left_index: Dict[Tuple[str, str, int, int], List[TreeNode]],
    right_index: Dict[Tuple[str, str, int, int], List[TreeNode]],
    matches: List[Tuple[Tuple[str, str, int, int], Tuple[str, str, int, int]]],
    ops: List[DiffOp],
) -> Tuple[Counter[str], Counter[str], Dict[str, int]]:
    """
    Implement the taxonomy table predicates as faithfully as possible using:
      - gumtree parse trees (for NodeType/NodeValue/Parent/Ancestor)
      - gumtree textdiff matches and operations (Insert/Delete/Update/Move/Match)
      - lightweight semantic facts (Def/Use/LocalCall/InstanceCall/Exception)
    Returns (fine_types, main_categories, action_counts)
    """
    fine_counts: Counter[str] = Counter()
    main_counts: Counter[str] = Counter()
    action_counts: Dict[str, int] = {}

    # Build match maps
    l2r: Dict[Tuple[str, str, int, int], Tuple[str, str, int, int]] = {}
    r2l: Dict[Tuple[str, str, int, int], Tuple[str, str, int, int]] = {}
    for lk, rk in matches:
        l2r[lk] = rk
        r2l[rk] = lk

    # Semantic analyses
    sem_l = _analyze_semantics(left_root)
    sem_r = _analyze_semantics(right_root)

    defs_l = sem_l["defs"]
    uses_l = sem_l["uses"]
    defs_r = sem_r["defs"]
    uses_r = sem_r["uses"]
    local_l = sem_l["local_calls"]
    inst_l = sem_l["instance_calls"]
    local_r = sem_r["local_calls"]
    inst_r = sem_r["instance_calls"]
    exc_l = set(sem_l["exceptions"]) | set(sem_l["thrown"])
    exc_r = set(sem_r["exceptions"]) | set(sem_r["thrown"])

    # Precompute finally nodes in right tree
    finally_nodes_r = _find_finally_block_nodes(right_root)

    # Utility to resolve a "changed node" to its right-side node (for ancestor predicates)
    def resolve_right_from_left(lk: Tuple[str, str, int, int]) -> Optional[TreeNode]:
        rk = l2r.get(lk)
        if not rk:
            return None
        return _pick_one(right_index, rk)

    def resolve_right(k: Optional[Tuple[str, str, int, int]]) -> Optional[TreeNode]:
        if not k:
            return None
        return _pick_one(right_index, k)

    def resolve_left(k: Optional[Tuple[str, str, int, int]]) -> Optional[TreeNode]:
        if not k:
            return None
        return _pick_one(left_index, k)

    # Collect structural operation sets
    inserted: List[TreeNode] = []
    deleted: List[TreeNode] = []
    updated_left: List[Tuple[TreeNode, Optional[TreeNode]]] = []
    moved: List[Tuple[TreeNode, Optional[TreeNode]]] = []

    for op in ops:
        action_counts[op.kind] = action_counts.get(op.kind, 0) + 1
        if op.kind == "insert" and op.right:
            n = resolve_right(op.right)
            if n:
                inserted.append(n)
        elif op.kind == "delete" and op.left:
            n = resolve_left(op.left)
            if n:
                deleted.append(n)
        elif op.kind == "update" and op.left:
            lnode = resolve_left(op.left)
            rnode = resolve_right_from_left(op.left)
            if lnode:
                updated_left.append((lnode, rnode))
        elif op.kind == "move" and op.left:
            lnode = resolve_left(op.left)
            rnode = resolve_right_from_left(op.left)
            if lnode:
                moved.append((lnode, rnode))

    def bump(label: str, inc: int = 1) -> None:
        if inc <= 0:
            return
        fine_counts[label] += inc
        main_counts[_main_category_from_type(label)] += inc

    # ---- Code Hardening ----
    bump("Code Hardening: Add a conditional", sum(1 for n in inserted if n.type == "IfStatement"))
    bump("Code Hardening: Insert a final modifier", sum(1 for n in inserted if n.type == "Modifier" and n.label == "final"))

    # Handle a new exception type: Exception(e, right) and not Exception(e, left)
    bump(
        "Code Hardening: Handle a new exception type",
        sum(1 for e in exc_r if e not in exc_l),
    )

    # Clean up unmanaged resources: clean method call appears in right but not left
    clean_r = {m for m in (local_r | inst_r) if _is_clean_method(m)}
    clean_l = {m for m in (local_l | inst_l) if _is_clean_method(m)}
    bump(
        "Code Hardening: Clean up unmanaged resources",
        sum(1 for m in clean_r if m not in clean_l),
    )

    # ---- Resolve Compilation Errors ----
    # Declare an undeclared variable: inserted variable decl v, Use(v,left) and not Def(v,left)
    def _extract_decl_name(n: TreeNode) -> Optional[str]:
        if n.type in {"VariableDeclarationFragment", "SingleVariableDeclaration"}:
            for c in n.children:
                if c.type == "SimpleName" and c.label:
                    return c.label
        # fallback: look for first SimpleName in subtree
        for d in _descendants(n):
            if d.type == "SimpleName" and d.label:
                return d.label
        return None

    declared_undeclared_var = 0
    for n in inserted:
        if n.type in {
            "VariableDeclarationFragment",
            "SingleVariableDeclaration",
            "VariableDeclarationStatement",
            "VariableDeclarationExpression",
            "FieldDeclaration",
        }:
            v = _extract_decl_name(n)
            if v and (v in uses_l) and (v not in defs_l):
                declared_undeclared_var += 1

    bump("Resolve Compilation Errors: Declare an undeclared variable", declared_undeclared_var)

    # Specify a target of method invocation: InstanceCall(m,right) and LocalCall(m,left)
    bump(
        "Resolve Compilation Errors: Specify a target of method invocation",
        sum(1 for m in local_l if m in inst_r),
    )

    # Remove undeclared variables or local method calls
    undef_uses_l = {v for v in uses_l if v not in defs_l}
    removed_undef = sum(1 for v in undef_uses_l if v not in uses_r)
    removed_local = sum(1 for m in local_l if m not in local_r and m not in inst_r)
    bump(
        "Resolve Compilation Errors: Remove undeclared variables or local method calls",
        removed_undef + removed_local,
    )

    # ---- Exception Handling ----
    bump(
        "Exception Handling: Insert/delete a try-catch block",
        sum(1 for n in inserted if n.type == "TryStatement") + sum(1 for n in deleted if n.type == "TryStatement"),
    )

    # Thrown exception changes (best-effort set difference)
    thrown_delta = [e for e in (exc_l ^ exc_r) if _is_exception_type(e)]
    bump("Exception Handling: Insert/delete a thrown exception in a method header", len(thrown_delta))

    updated_exc_type = 0
    for lnode, rnode in updated_left:
        if lnode.type in {"SimpleType", "QualifiedType"} and rnode and rnode.type in {"SimpleType", "QualifiedType"}:
            if _is_exception_type(lnode.label) and _is_exception_type(rnode.label) and lnode.label != rnode.label:
                updated_exc_type += 1

    # GumTree may also surface exception type updates at the `SimpleName` under `SimpleType/QualifiedType`,
    # not at the `SimpleType` itself. Count those too (catch parameter types / thrown types).
    for lnode, rnode in updated_left:
        if not rnode:
            continue
        if lnode.type not in {"SimpleName", "QualifiedName"} or rnode.type not in {"SimpleName", "QualifiedName"}:
            continue
        if lnode.label == rnode.label:
            continue
        lp = lnode.parent
        rp = rnode.parent
        if not lp or not rp:
            continue
        if lp.type not in {"SimpleType", "QualifiedType"} or rp.type not in {"SimpleType", "QualifiedType"}:
            continue
        if not (lp.has_ancestor_type("CatchClause") or lp.has_ancestor_type("MethodDeclaration")):
            continue
        if _is_exception_type(lnode.label) and _is_exception_type(rnode.label):
            updated_exc_type += 1

    bump("Exception Handling: Update the exception type", updated_exc_type)

    # Catch/finally statement changes: check changed nodes in right tree for ancestors
    def _is_in_catch(n: TreeNode) -> bool:
        return n.has_ancestor_type("CatchClause")

    def _is_in_finally(n: TreeNode) -> bool:
        return n.key in finally_nodes_r or any(a.key in finally_nodes_r for a in n.ancestors())

    changed_right_nodes: List[TreeNode] = []
    changed_right_nodes.extend(inserted)
    for _, rn in updated_left:
        if rn:
            changed_right_nodes.append(rn)
    for _, rn in moved:
        if rn:
            changed_right_nodes.append(rn)

    bump("Exception Handling: Change statements in a catch block", sum(1 for n in changed_right_nodes if _is_in_catch(n)))
    bump("Exception Handling: Change statements in a finally block", sum(1 for n in changed_right_nodes if _is_in_finally(n)))

    # ---- Logic Customization ----
    # Change a method call: Changed(t1) and has MethodInvocation ancestor in right tree
    bump(
        "Logic Customization: Change a method call",
        sum(1 for n in changed_right_nodes if (n.type == "MethodInvocation" or n.has_ancestor_type("MethodInvocation"))),
    )

    updated_const = 0
    for lnode, rnode in updated_left:
        if not rnode:
            continue
        if (lnode.type in {"NumberLiteral", "StringLiteral", "BooleanLiteral", "CharacterLiteral", "NullLiteral"} or lnode.type == "Literal") and (
            rnode.type in {"NumberLiteral", "StringLiteral", "BooleanLiteral", "CharacterLiteral", "NullLiteral"} or rnode.type == "Literal"
        ):
            updated_const += 1

    bump("Logic Customization: Update a constant value", updated_const)

    cond_anc_types = {
        "IfStatement",
        "ForStatement",
        "WhileStatement",
        "DoStatement",
        "EnhancedForStatement",
        "SwitchCase",
    }
    bump(
        "Logic Customization: Change a conditional expression",
        sum(1 for n in changed_right_nodes if any(a.type in cond_anc_types for a in n.ancestors())),
    )

    updated_var_type = 0
    for lnode, rnode in updated_left:
        if not rnode:
            continue
        type_nodes = {"PrimitiveType", "SimpleType", "QualifiedType", "ArrayType", "ParameterizedType"}
        if lnode.type in type_nodes and rnode.type in type_nodes:
            updated_var_type += 1

    bump("Logic Customization: Change the type of a variable", updated_var_type)

    # ---- Refactoring ----
    # Rename a variable/field/method: update SimpleName/QualifiedName with label change
    renamed = 0
    for lnode, rnode in updated_left:
        if not rnode:
            continue
        if lnode.type in {"SimpleName", "QualifiedName"} and rnode.type in {"SimpleName", "QualifiedName"} and lnode.label != rnode.label:
            renamed += 1

    bump("Refactoring: Rename a variable/field/method", renamed)

    # Replace hardcoded constant values with variables / Inline a field
    #
    # The taxonomy relies on `Match(t1, t2)` between surrounding nodes; in GumTree that corresponds to
    # the left-parent node being matched to the right-parent node. We approximate by:
    #   - map deleted child's left-parent -> matched right-parent
    #   - count matching inserted children under that right-parent
    literal_types = {"NumberLiteral", "StringLiteral", "BooleanLiteral", "CharacterLiteral", "NullLiteral", "TextBlock", "Literal"}
    name_types = {"SimpleName", "QualifiedName", "Name"}

    deleted_lit_by_rparent: Counter[Tuple[str, str, int, int]] = Counter()
    deleted_name_by_rparent: Counter[Tuple[str, str, int, int]] = Counter()
    inserted_name_by_parent: Counter[Tuple[str, str, int, int]] = Counter()
    inserted_lit_by_parent: Counter[Tuple[str, str, int, int]] = Counter()

    for n in deleted:
        if not n.parent:
            continue
        rp_key = l2r.get(n.parent.key)
        if not rp_key:
            continue
        if n.type in literal_types:
            deleted_lit_by_rparent[rp_key] += 1
        if n.type in name_types:
            deleted_name_by_rparent[rp_key] += 1

    for n in inserted:
        if not n.parent:
            continue
        pk = n.parent.key
        if n.type in name_types:
            inserted_name_by_parent[pk] += 1
        if n.type in literal_types:
            inserted_lit_by_parent[pk] += 1

    factor_out = 0
    for pkey, dl in deleted_lit_by_rparent.items():
        ins = inserted_name_by_parent.get(pkey, 0)
        if dl and ins:
            factor_out += min(dl, ins)
    bump("Refactoring: Replace hardcoded constant values with variables", factor_out)

    inline = 0
    for pkey, dn in deleted_name_by_rparent.items():
        ins = inserted_lit_by_parent.get(pkey, 0)
        if dn and ins:
            inline += min(dn, ins)
    bump("Refactoring: Inline a field", inline)

    # ---- Miscellaneous ----
    # Change access modifiers
    access_mods = {"private", "public", "protected", "static"}
    access_changes = sum(1 for n in (inserted + deleted) if n.type == "Modifier" and n.label in access_mods)
    for lnode, rnode in updated_left:
        if lnode.type == "Modifier" and lnode.label in access_mods:
            access_changes += 1
        if rnode and rnode.type == "Modifier" and rnode.label in access_mods:
            access_changes += 1
    bump("Miscellaneous: Change access modifiers", access_changes)

    # Change log/print statement
    log_changes = 0
    if any(
        (n.type == "MethodInvocation" and any(c.type == "SimpleName" and _is_log_method(c.label) for c in n.children))
        for n in changed_right_nodes
    ):
        log_changes += sum(
            1
            for n in changed_right_nodes
            if (n.type == "MethodInvocation" and any(c.type == "SimpleName" and _is_log_method(c.label) for c in n.children))
        )
    else:
        # also catch updates to method-name SimpleName nodes under MethodInvocation
        for lnode, rnode in updated_left:
            if rnode and rnode.type == "SimpleName" and rnode.parent and rnode.parent.type == "MethodInvocation" and _is_log_method(rnode.label):
                log_changes += 1

    bump("Miscellaneous: Change a log/print statement", log_changes)

    # Style reformatting (curly braces): any changed Block under MethodDeclaration
    bump(
        "Miscellaneous: Style reformatting",
        sum(1 for n in changed_right_nodes if n.type == "Block" and n.parent and n.parent.type == "MethodDeclaration"),
    )

    # Change Java annotations / code comments
    bump("Miscellaneous: Change Java annotations", sum(1 for n in (inserted + deleted) if n.type == "Annotation"))
    bump("Miscellaneous: Change code comments", sum(1 for n in (inserted + deleted) if n.type == "Comment"))

    return fine_counts, main_counts, action_counts


def _extract_actions(payload: Dict[str, Any]) -> List[Tuple[str, GTNode]]:
    actions = []
    for act in payload.get("actions", []):
        raw_action = (act.get("action") or "").lower()
        # v3 actions are like insert-node/insert-tree/delete-*/update-node/move-tree
        action = raw_action.split("-", 1)[0] if raw_action else ""
        node_type, node_label = _parse_tree_repr(act.get("tree") or "")
        parent_type, _ = _parse_tree_repr(act.get("parent") or "")
        actions.append(
            (
                action,
                GTNode(
                    type=node_type,
                    label=node_label,
                    parent_type=parent_type,
                ),
            )
        )
    return actions


def _categorize(actions: List[Tuple[str, GTNode]]) -> Set[str]:
    cats: Set[str] = set()

    for action, node in actions:
        ntype = node.type
        label = node.label
        parent = node.parent_type

        # Code Hardening
        if action == "insert" and ntype == "IfStatement":
            cats.add("Code Hardening: Add a conditional")
        if action == "insert" and ntype == "Modifier" and label == "final":
            cats.add("Code Hardening: Insert a final modifier")
        # Note: method identifiers often appear as SimpleName in GumTree JSON.
        if action == "insert" and ntype in {"MethodInvocation", "SuperMethodInvocation", "SimpleName"} and label in {"close", "dispose", "recycle"}:
            cats.add("Code Hardening: Clean up unmanaged resources")

        # Resolve Compilation Errors
        if action == "insert" and ntype in {
            "VariableDeclaration",
            "VariableDeclarationStatement",
            "VariableDeclarationFragment",
            "VariableDeclarationExpression",
            "FieldDeclaration",
            "SingleVariableDeclaration",
        }:
            cats.add("Resolve Compilation Errors: Declare an undeclared variable")
        if action == "update" and ntype in {"MethodInvocation", "SuperMethodInvocation"}:
            cats.add("Resolve Compilation Errors: Specify a target of method invocation")
        if action == "delete" and ntype in {"Name", "SimpleName"} and parent in {"MethodInvocation", "SimpleName"}:
            cats.add("Resolve Compilation Errors: Remove undeclared variables or local method calls")

        # Exception Handling
        if action in {"insert", "delete"} and ntype == "TryStatement":
            cats.add("Exception Handling: Insert/delete a try-catch block")
        if action == "update" and ntype in {"SimpleType", "QualifiedType"} and parent in {"CatchClause", "MethodDeclaration"}:
            cats.add("Exception Handling: Update the exception type")
        if action == "change" and ntype in {"CatchClause", "FinallyBlock"}:
            cats.add("Exception Handling: Change statements in catch/finally block")

        # Logic Customization
        if action == "update" and (ntype in {"Literal", "NumberLiteral", "StringLiteral", "BooleanLiteral", "CharacterLiteral", "NullLiteral", "TextBlock"}):
            cats.add("Logic Customization: Update a constant value")
        if action == "update" and ntype in {"InfixExpression", "ConditionalExpression", "PrefixExpression"}:
            cats.add("Logic Customization: Change a conditional expression")
        # Taxonomy-inspired approximation of:
        #   Changed(t1) ∧ Ancestor(t2, t1) ∧ NodeType(t2, MethodInvocation)
        # GumTree's JSON textdiff client provides only the changed node plus (often) its direct parent.
        # We treat "parent_type == MethodInvocation" as the smallest available "Ancestor" predicate.
        if action == "update" and (
            ntype in {"MethodInvocation", "SuperMethodInvocation"} or parent in {"MethodInvocation", "SuperMethodInvocation"}
        ):
            cats.add("Logic Customization: Change a method call")
        if action == "update" and ntype in {"SimpleType", "QualifiedType"} and parent in {"VariableDeclaration", "FieldDeclaration"}:
            cats.add("Logic Customization: Change the type of a variable")

        # Refactoring
        if action == "update" and ntype in {"SimpleName", "QualifiedName"}:
            cats.add("Refactoring: Rename a variable/field/method")
        if action == "update" and ntype == "Modifier" and label in {"public", "protected", "private", "static"}:
            cats.add("Refactoring: Change access modifiers")

        # Miscellaneous
        if action in {"insert", "update", "delete"} and ntype == "Annotation":
            cats.add("Miscellaneous: Change Java annotations")
        if action in {"insert", "update", "delete"} and ntype == "Comment":
            cats.add("Miscellaneous: Change code comments")
        if action == "update" and ntype in {"MethodInvocation", "SuperMethodInvocation", "SimpleName"} and _is_log_method(label):
            cats.add("Miscellaneous: Change a log/print statement")

    return cats


def _main_categories(cats: Set[str]) -> List[str]:
    """
    Collapse fine-grained labels like 'Refactoring: Rename ...' into the 6 main
    categories by taking the prefix before ':'.
    """
    main: Set[str] = set()
    for c in cats:
        if ":" in c:
            main.add(c.split(":", 1)[0].strip())
        else:
            main.add(c.strip())
    return sorted(main)


def _action_counts(actions: List[Tuple[str, GTNode]]) -> Dict[str, int]:
    counts: Dict[str, int] = {}
    for action, _ in actions:
        if not action:
            continue
        counts[action] = counts.get(action, 0) + 1
    return counts


def main() -> None:
    parser = argparse.ArgumentParser(description="Classify adaptations using GumTree AST edits.")
    parser.add_argument("--dataset", required=True, help="Dataset JSON path.")
    parser.add_argument("--output", default="", help="Optional output JSON path (per-record taxonomy rows).")
    parser.add_argument(
        "--in-place",
        action="store_true",
        help="Write gumtree categories back into the dataset JSON (in-place).",
    )
    parser.add_argument(
        "--skip-failures",
        action="store_true",
        help="If GumTree fails for a record, mark it as parse_failed and continue (default is fail-fast).",
    )
    parser.add_argument("--gumtree", default=_default_gumtree_bin(), help="Path to gumtree CLI.")
    parser.add_argument("--limit", type=int, default=0, help="Limit number of records (0 = all).")
    args = parser.parse_args()

    dataset_path = Path(args.dataset)
    data = json.loads(dataset_path.read_text(encoding="utf-8"))

    total = min(args.limit, len(data)) if args.limit else len(data)
    iterable = data[: args.limit] if args.limit else data

    results: List[Dict[str, Any]] = []
    for record in tqdm(iterable, total=total, desc="gumtree-taxonomy"):
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
            if args.in_place:
                record["gumtree_status"] = "missing_input"
            results.append(row)
            continue

        left_code = _wrap_java(gh_snippet)
        right_code = _wrap_java(adapted)
        tmp_root = Path.home() / "tmp" / "gumtree_taxonomy"
        tmp_root.mkdir(parents=True, exist_ok=True)
        with tempfile.TemporaryDirectory(dir=str(tmp_root)) as tmpdir:
            left_path = Path(tmpdir) / "left.java"
            right_path = Path(tmpdir) / "right.java"
            left_path.write_text(left_code, encoding="utf-8")
            right_path.write_text(right_code, encoding="utf-8")
            try:
                # Exact taxonomy implementation uses gumtree parse trees + textdiff operations/matches.
                left_tree_txt = _run_parse_tree(args.gumtree, left_path)
                right_tree_txt = _run_parse_tree(args.gumtree, right_path)
                left_root, left_index = _build_tree(left_tree_txt)
                right_root, right_index = _build_tree(right_tree_txt)
                diff_txt = _run_textdiff(args.gumtree, left_path, right_path)
                matches, ops = _parse_textdiff(diff_txt)
                fine_counts, main_counts, action_counts = _categorize_exact(
                    left_root, right_root, left_index, right_index, matches, ops
                )
                row["categories"] = sorted(fine_counts.keys())
                row["main_categories"] = sorted(main_counts.keys())
                row["fine_category_counts"] = dict(fine_counts)
                row["main_category_counts"] = dict(main_counts)
                row["action_counts"] = action_counts

                if args.in_place:
                    record["gumtree_status"] = "ok"
                    record["gumtree_fine_categories"] = row["categories"]
                    record["gumtree_main_categories"] = row["main_categories"]
                    record["gumtree_fine_category_counts"] = row["fine_category_counts"]
                    record["gumtree_main_category_counts"] = row["main_category_counts"]
                    record["gumtree_action_counts"] = row["action_counts"]
            except Exception as e:
                row["status"] = "parse_failed"
                row["error"] = str(e)
                if args.in_place:
                    record["gumtree_status"] = "parse_failed"
                    record.pop("gumtree_fine_categories", None)
                    record.pop("gumtree_main_categories", None)
                    record.pop("gumtree_fine_category_counts", None)
                    record.pop("gumtree_main_category_counts", None)
                    record.pop("gumtree_action_counts", None)
                    # Keep the dataset small: store only a short error summary.
                    record["gumtree_error"] = (str(e) or "")[:200]
                if not args.skip_failures:
                    raise

        results.append(row)

    if args.in_place:
        dataset_path.write_text(json.dumps(data, indent=2, ensure_ascii=True), encoding="utf-8")

    if args.output:
        output_path = Path(args.output)
        output_path.parent.mkdir(parents=True, exist_ok=True)
        output_path.write_text(json.dumps(results, indent=2, ensure_ascii=True), encoding="utf-8")


if __name__ == "__main__":
    main()
