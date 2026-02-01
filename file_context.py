from __future__ import annotations

from collections import defaultdict
from typing import Dict, List, Optional, Set

import javalang

from file_call_graph import build_intra_file_graph, extract_field_usage


def _type_arg_to_str(arg) -> str:
    if arg is None:
        return "?"
    if getattr(arg, "type", None) is None:
        return "?"
    base = _type_to_str(arg.type)
    if arg.pattern_type == "extends":
        return f"? extends {base}"
    if arg.pattern_type == "super":
        return f"? super {base}"
    return base


def _type_to_str(t) -> str:
    if t is None:
        return "void"
    name = t.name
    if getattr(t, "arguments", None):
        args = ", ".join(_type_arg_to_str(a) for a in t.arguments)
        name = f"{name}<{args}>"
    if getattr(t, "dimensions", None):
        name += "[]" * len(t.dimensions)
    return name


def _safe_parse(source: str) -> Optional[javalang.tree.CompilationUnit]:
    try:
        return javalang.parse.parse(source)
    except Exception:
        return None


def _throw_to_str(t) -> str:
    if isinstance(t, str):
        return t
    return getattr(t, "name", str(t))


def _extract_target_signature(gh_snippet: str) -> Dict[str, str]:
    if not gh_snippet.strip():
        return {}
    source = gh_snippet
    if "class" not in gh_snippet:
        source = f"public class Dummy {{\n{gh_snippet}\n}}"
    tree = _safe_parse(source)
    if tree is None:
        return {}

    for _, node in tree.filter(javalang.tree.MethodDeclaration):
        params = [_type_to_str(p.type) for p in node.parameters]
        ret = _type_to_str(node.return_type)
        throws = [_throw_to_str(t) for t in (node.throws or [])]
        signature = f"{ret} {node.name}({', '.join(params)})"
        return {
            "name": node.name,
            "return_type": ret,
            "parameters": params,
            "throws": throws,
            "modifiers": sorted(node.modifiers),
            "signature": signature,
        }
    for _, node in tree.filter(javalang.tree.ConstructorDeclaration):
        params = [_type_to_str(p.type) for p in node.parameters]
        signature = f"{node.name}({', '.join(params)})"
        return {
            "name": node.name,
            "return_type": "",
            "parameters": params,
            "throws": [_throw_to_str(t) for t in (node.throws or [])],
            "modifiers": sorted(node.modifiers),
            "signature": signature,
        }
    return {}


def _extract_snippet_callees(gh_snippet: str, method_names: Set[str]) -> List[str]:
    if not gh_snippet.strip() or not method_names:
        return []
    source = gh_snippet
    if "class" not in gh_snippet:
        source = f"public class Dummy {{\n{gh_snippet}\n}}"
    tree = _safe_parse(source)
    if tree is None:
        return []

    for _, node in tree.filter(javalang.tree.MethodDeclaration):
        callees = set()
        for _, inv in node.filter(javalang.tree.MethodInvocation):
            if inv.member in method_names:
                callees.add(inv.member)
        for _, inv in node.filter(javalang.tree.SuperMethodInvocation):
            if inv.member in method_names:
                callees.add(inv.member)
        return sorted(callees)
    return []


def _extract_class_info(tree: javalang.tree.CompilationUnit) -> List[Dict]:
    classes = []
    for type_decl in tree.types:
        if not isinstance(type_decl, javalang.tree.ClassDeclaration):
            continue
        classes.append(
            {
                "name": type_decl.name,
                "extends": type_decl.extends.name if type_decl.extends else "",
                "implements": [impl.name for impl in type_decl.implements or []],
                "modifiers": sorted(type_decl.modifiers),
                "annotations": [ann.name for ann in type_decl.annotations or []],
            }
        )
    return classes


def _extract_fields(tree: javalang.tree.CompilationUnit) -> List[Dict]:
    fields = []
    for _, field in tree.filter(javalang.tree.FieldDeclaration):
        ftype = _type_to_str(field.type)
        for declarator in field.declarators:
            fields.append(
                {
                    "name": declarator.name,
                    "type": ftype,
                    "modifiers": sorted(field.modifiers),
                }
            )
    return fields


def _extract_methods(tree: javalang.tree.CompilationUnit) -> List[Dict]:
    methods = []
    for _, node in tree.filter(javalang.tree.MethodDeclaration):
        params = [_type_to_str(p.type) for p in node.parameters]
        ret = _type_to_str(node.return_type)
        signature = f"{ret} {node.name}({', '.join(params)})"
        methods.append(
            {
                "name": node.name,
                "return_type": ret,
                "parameters": params,
                "modifiers": sorted(node.modifiers),
                "annotations": [ann.name for ann in node.annotations or []],
                "throws": [_throw_to_str(t) for t in (node.throws or [])],
                "signature": signature,
            }
        )
    for _, node in tree.filter(javalang.tree.ConstructorDeclaration):
        params = [_type_to_str(p.type) for p in node.parameters]
        signature = f"{node.name}({', '.join(params)})"
        methods.append(
            {
                "name": node.name,
                "return_type": "",
                "parameters": params,
                "modifiers": sorted(node.modifiers),
                "annotations": [ann.name for ann in node.annotations or []],
                "throws": [_throw_to_str(t) for t in (node.throws or [])],
                "signature": signature,
            }
        )
    return methods


def _bfs_hops(
    start: str, adjacency: Dict[str, Set[str]], max_hops: int
) -> Dict[int, List[str]]:
    if not start or max_hops <= 0:
        return {}

    hops: Dict[int, List[str]] = {}
    visited = {start}
    frontier = {start}

    for depth in range(1, max_hops + 1):
        next_frontier = set()
        for node in frontier:
            next_frontier.update(adjacency.get(node, set()))
        next_frontier -= visited
        if not next_frontier:
            break
        hops[depth] = sorted(next_frontier)
        visited.update(next_frontier)
        frontier = next_frontier

    return hops


def build_file_context_summary(
    file_code: str,
    gh_snippet: str,
    max_hops: int = 1,
) -> Dict:
    tree = _safe_parse(file_code)
    if tree is None:
        return {"error": "javalang parse failed"}

    package_name = tree.package.name if tree.package else ""
    imports = [
        f"{imp.path}{'.*' if imp.wildcard else ''}"
        for imp in (tree.imports or [])
    ]
    custom_api_imports = [
        imp
        for imp in imports
        if not (
            imp.startswith("java.")
            or imp.startswith("javax.")
            or imp.startswith("android.")
        )
    ]
    classes = _extract_class_info(tree)
    class_names = [c["name"] for c in classes]
    fields = _extract_fields(tree)
    field_types = {f["name"]: f["type"] for f in fields}
    methods = _extract_methods(tree)
    method_names = [m["name"] for m in methods]
    field_names = [f["name"] for f in fields]

    target = _extract_target_signature(gh_snippet)
    target_name = target.get("name", "")

    graph_names = set(method_names)
    if target_name:
        graph_names.add(target_name)

    edges, caller_map = build_intra_file_graph(file_code, graph_names)
    caller_to_callees: Dict[str, Set[str]] = defaultdict(set)
    for caller, callees in caller_map.items():
        caller_to_callees[caller].update(callees)

    snippet_callees = _extract_snippet_callees(gh_snippet, set(method_names))
    if target_name:
        caller_to_callees[target_name].update(snippet_callees)

    callee_to_callers: Dict[str, Set[str]] = defaultdict(set)
    for caller, callees in caller_to_callees.items():
        for callee in callees:
            callee_to_callers[callee].add(caller)

    upstream_hops = _bfs_hops(target_name, callee_to_callers, max_hops)
    downstream_hops = _bfs_hops(target_name, caller_to_callees, max_hops)

    direct_callers = sorted(upstream_hops.get(1, []))
    direct_callees = sorted(downstream_hops.get(1, []))

    upstream_methods = {m for names in upstream_hops.values() for m in names}
    downstream_methods = {m for names in downstream_hops.values() for m in names}
    related_methods = sorted(
        (upstream_methods | downstream_methods)
        - set(direct_callers)
        - set(direct_callees)
    )

    context_methods = {target_name} | set(direct_callers) | set(direct_callees) | set(
        related_methods
    )
    context_methods.discard("")

    call_pairs = sorted(
        {
            f"{caller} -> {callee}"
            for caller, callees in caller_to_callees.items()
            for callee in callees
            if caller in context_methods and callee in context_methods
        }
    )

    caller_callee_map = {
        caller: sorted(
            [
                callee
                for callee in callees
                if callee in context_methods
            ]
        )
        for caller, callees in caller_to_callees.items()
        if caller in context_methods
    }

    field_usage = extract_field_usage(file_code, field_names)
    context_fields = set()
    for name in context_methods:
        context_fields.update(field_usage.get(name, []))

    context_field_types = [
        f"{name}: {field_types.get(name, '')}".rstrip(": ")
        for name in sorted(context_fields)
    ]

    return {
        "package": package_name,
        "classes": class_names,
        "imports": imports,
        "custom_api_imports": custom_api_imports,
        "direct_callers": direct_callers,
        "direct_callees": direct_callees,
        "related_methods": related_methods,
        "call_pairs": call_pairs,
        "caller_callee_map": caller_callee_map,
        "context_fields": sorted(context_fields),
        "context_field_types": context_field_types,
        "max_hops": max_hops,
    }
