from __future__ import annotations

from collections import defaultdict
from typing import Dict, Iterable, List, Set, Tuple

import javalang


def _safe_parse(source: str):
    try:
        return javalang.parse.parse(source)
    except Exception:
        return None


def build_intra_file_graph(
    source: str, method_names: Iterable[str]
) -> Tuple[List[Tuple[str, str]], Dict[str, List[str]]]:
    tree = _safe_parse(source)
    if tree is None:
        return [], {}

    names = set(method_names)
    edges: List[Tuple[str, str]] = []
    caller_map: Dict[str, Set[str]] = defaultdict(set)

    for _, method in tree.filter(javalang.tree.MethodDeclaration):
        caller = method.name
        callees = set()
        for _, node in method.filter(javalang.tree.MethodInvocation):
            if node.member in names:
                callees.add(node.member)
        for _, node in method.filter(javalang.tree.SuperMethodInvocation):
            if node.member in names:
                callees.add(node.member)
        for callee in sorted(callees):
            edges.append((caller, callee))
            caller_map[caller].add(callee)

    return edges, {k: sorted(v) for k, v in caller_map.items()}


def extract_field_usage(source: str, field_names: Iterable[str]) -> Dict[str, List[str]]:
    tree = _safe_parse(source)
    if tree is None:
        return {}

    fields = set(field_names)
    usage: Dict[str, Set[str]] = defaultdict(set)

    for _, method in tree.filter(javalang.tree.MethodDeclaration):
        used = set()
        for _, node in method.filter(javalang.tree.MemberReference):
            if node.member in fields:
                used.add(node.member)
        usage[method.name] = used

    return {k: sorted(v) for k, v in usage.items()}
