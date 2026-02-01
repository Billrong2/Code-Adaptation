#!/usr/bin/env python3
from __future__ import annotations

import argparse
import json
import re
import subprocess
from collections import Counter
from pathlib import Path
from typing import Any, Dict, List, Optional, Tuple


ROOT = Path(__file__).resolve().parent
DEFAULT_DATASET = ROOT / "data" / "adaptations_with_snapshots_with_intent_and_results_marked.json"
DEFAULT_JUDGED = ROOT / "data" / "adaptations_with_snapshots_with_intent_and_results_llm_judged.json"


def _read_json(path: Path) -> List[Dict[str, Any]]:
    return json.loads(path.read_text(encoding="utf-8"))


def _summarize_similarity(
    entries: List[Dict[str, Any]],
    label: str,
    has_tests_key: str,
    manual_key: str,
    test_key: str,
    edit_key: str,
    bleu_key: str,
    ast_key: str,
) -> None:
    buckets = {"test:Pass": [], "test:Fail": [], "manual:Corr": [], "manual:Incorr": []}
    counts = {k: 0 for k in buckets}
    bleu_buckets = {k: [] for k in buckets}
    ast_buckets = {k: [] for k in buckets}
    for rec in entries:
        has_tests = bool(rec.get(has_tests_key))
        manual = rec.get(manual_key)
        test_outcome = rec.get(test_key)
        edit = rec.get(edit_key)
        bleu = rec.get(bleu_key)
        ast = rec.get(ast_key)
        b = _bucket_key(has_tests, manual, test_outcome)
        if not b:
            continue
        counts[b] += 1
        if edit is not None:
            buckets[b].append(edit)
        if bleu is not None:
            bleu_buckets[b].append(bleu)
        if ast is not None:
            ast_buckets[b].append(ast)

    print(f"\n{label}:")
    for key in ["test:Pass", "test:Fail", "manual:Corr", "manual:Incorr"]:
        edits = buckets.get(key, [])
        bleus = bleu_buckets.get(key, [])
        asts = ast_buckets.get(key, [])
        n = counts.get(key, 0)
        edit_avg = sum(edits) / len(edits) if edits else 0.0
        bleu_avg = sum(bleus) / len(bleus) if bleus else 0.0
        ast_avg = sum(asts) / len(asts) if asts else 0.0
        print(f"  {key}  n={n}  EditSim={edit_avg:.4f}  BLEU={bleu_avg:.4f}  AST={ast_avg:.4f}")


def _bucket_key(
    has_tests: bool, manual: Optional[str], test_outcome: Optional[str]
) -> Optional[str]:
    if has_tests and test_outcome in ("Pass", "Fail"):
        return f"test:{test_outcome}"
    if (not has_tests) and manual in ("Corr", "Incorr"):
        return f"manual:{manual}"
    return None


def _summarize_gumtree(
    entries: List[Dict[str, Any]],
    label: str,
    has_tests_key: str,
    manual_key: str,
    test_key: str,
    status_key: str = "gumtree_status",
    cats_key: str = "gumtree_main_categories",
) -> None:
    statuses = Counter(rec.get(status_key, "<missing>") for rec in entries)

    print(f"\n{label} GumTree status:")
    for k in sorted(statuses.keys()):
        print(f"  {k}: {statuses[k]}")

    main_cats = [
        "Code Hardening",
        "Resolve Compilation Errors",
        "Exception Handling",
        "Logic Customization",
        "Refactoring",
        "Miscellaneous",
    ]
    buckets = ["test:Pass", "test:Fail", "manual:Corr", "manual:Incorr"]
    bucket_totals = {b: 0 for b in buckets}  # records in bucket (for denominator)
    bucket_no_gumtree = {b: 0 for b in buckets}  # gumtree not available for that record
    bucket_presence: Dict[str, Counter] = {b: Counter() for b in buckets}  # records containing category
    bucket_occ: Dict[str, Counter] = {b: Counter() for b in buckets}  # total occurrences (can exceed #records)

    for rec in entries:
        manual = rec.get(manual_key)
        has_tests = bool(rec.get(has_tests_key))
        test_outcome = rec.get(test_key)
        b = _bucket_key(has_tests, manual, test_outcome)
        if not b:
            continue
        bucket_totals[b] += 1
        status = rec.get(status_key, "<missing>")
        cats = rec.get(cats_key) or []
        counts_dict = rec.get("gumtree_main_category_counts") or {}
        if status != "ok" or (not cats and not counts_dict):
            bucket_no_gumtree[b] += 1
            continue
        # Presence counts (per record)
        present = set()
        for c in cats:
            present.add(c if c in main_cats else "<other>")
        for c in present:
            bucket_presence[b][c] += 1

        # Occurrence counts (per record can contribute multiple times)
        if isinstance(counts_dict, dict) and counts_dict:
            for c, v in counts_dict.items():
                key = c if c in main_cats else "<other>"
                try:
                    bucket_occ[b][key] += int(v)
                except Exception:
                    bucket_occ[b][key] += 0
        else:
            # Fallback: treat each category as occurring once.
            for c in cats:
                bucket_occ[b][c if c in main_cats else "<other>"] += 1

    print(f"\n{label} adaptation categories:")
    for b in buckets:
        n = bucket_totals[b]
        print(f"  {b}  n={n}")
        for c in main_cats:
            pres = bucket_presence[b].get(c, 0)
            occ = bucket_occ[b].get(c, 0)
            avg = (occ / n) if n else 0.0
            print(f"    {c}: records={pres}  occ={occ}  avg/rec={avg:.2f}")
        if bucket_occ[b].get("<other>", 0) or bucket_presence[b].get("<other>", 0):
            pres = bucket_presence[b].get("<other>", 0)
            occ = bucket_occ[b].get("<other>", 0)
            avg = (occ / n) if n else 0.0
            print(f"    <other>: records={pres}  occ={occ}  avg/rec={avg:.2f}")
        if bucket_no_gumtree[b]:
            print(f"    <no-gumtree>: {bucket_no_gumtree[b]}")

    # Sub-categories (fine taxonomy labels).
    fine_key = "gumtree_fine_categories"
    fine_counts_key = "gumtree_fine_category_counts"
    fine_presence: Dict[str, Counter] = {b: Counter() for b in buckets}
    fine_occ: Dict[str, Counter] = {b: Counter() for b in buckets}
    fine_parse_failed: Dict[str, int] = {b: 0 for b in buckets}
    fine_not_present: Dict[str, int] = {b: 0 for b in buckets}
    for rec in entries:
        manual = rec.get(manual_key)
        has_tests = bool(rec.get(has_tests_key))
        test_outcome = rec.get(test_key)
        b = _bucket_key(has_tests, manual, test_outcome)
        if not b:
            continue
        status = rec.get(status_key, "<missing>")
        fine = rec.get(fine_key)
        fine_counts_dict = rec.get(fine_counts_key)
        if status != "ok":
            fine_parse_failed[b] += 1
            continue
        if (not fine) and (not fine_counts_dict):
            fine_not_present[b] += 1
            continue
        if fine:
            for t in set(fine):
                fine_presence[b][t] += 1
        if isinstance(fine_counts_dict, dict) and fine_counts_dict:
            for t, v in fine_counts_dict.items():
                try:
                    fine_occ[b][t] += int(v)
                except Exception:
                    fine_occ[b][t] += 0
        else:
            for t in (fine or []):
                fine_occ[b][t] += 1

    print(f"\n{label} adaptation sub-categories:")
    ordered_mains = [
        "Code Hardening",
        "Resolve Compilation Errors",
        "Exception Handling",
        "Logic Customization",
        "Refactoring",
        "Miscellaneous",
    ]
    for b in buckets:
        n = bucket_totals[b]
        print(f"  {b}  n={n}")
        # Print grouped by main category prefix if possible
        for m in ordered_mains:
            keys = [k for k in fine_occ[b].keys() if k.startswith(m + ":")]
            if not keys:
                continue
            print(f"    {m}:")
            for k in sorted(keys, key=lambda kk: (-fine_occ[b][kk], kk)):
                pres = fine_presence[b].get(k, 0)
                occ = fine_occ[b].get(k, 0)
                avg = (occ / n) if n else 0.0
                print(f"      {k.split(':', 1)[1].strip()}: records={pres}  occ={occ}  avg/rec={avg:.2f}")
        other_keys = [k for k in fine_occ[b].keys() if ":" not in k]
        if other_keys:
            print("    <other>:")
            for k in sorted(other_keys, key=lambda kk: (-fine_occ[b][kk], kk)):
                pres = fine_presence[b].get(k, 0)
                occ = fine_occ[b].get(k, 0)
                avg = (occ / n) if n else 0.0
                print(f"      {k}: records={pres}  occ={occ}  avg/rec={avg:.2f}")
        if fine_parse_failed[b]:
            print(f"    <gumtree_failed_or_missing_input>: {fine_parse_failed[b]}")
        if fine_not_present[b]:
            print(f"    <fine_categories_missing_rerun_gumtree>: {fine_not_present[b]}")

def _summarize_pass_correct_gumtree(
    entries: List[Dict[str, Any]],
    label: str,
    has_tests_key: str,
    manual_key: str,
    test_key: str,
    status_key: str = "gumtree_status",
    main_counts_key: str = "gumtree_main_category_counts",
    fine_counts_key: str = "gumtree_fine_category_counts",
) -> None:
    """
    Aggregate GumTree taxonomy for all "passed/correct" instances:
      - test:Pass  (has_tests_repo == True and manual == Corr)
      - manual:Corr (has_tests_repo == False and manual == Corr)

    Prints total occurrences and per-record averages; totals can exceed #records.
    """
    ordered_mains = [
        "Code Hardening",
        "Resolve Compilation Errors",
        "Exception Handling",
        "Logic Customization",
        "Refactoring",
        "Miscellaneous",
    ]

    passed_records: List[Dict[str, Any]] = []
    for r in entries:
        has_tests = bool(r.get(has_tests_key))
        if has_tests:
            if r.get(test_key) != "Pass":
                continue
        else:
            if r.get(manual_key) != "Corr":
                continue
        passed_records.append(r)

    n = len(passed_records)
    statuses = Counter(r.get(status_key, "<missing>") for r in passed_records)
    ok_records = sum(1 for r in passed_records if r.get(status_key) == "ok")

    # Aggregate main + fine
    main_occ: Counter[str] = Counter()
    main_pres: Counter[str] = Counter()
    fine_occ: Counter[str] = Counter()
    fine_pres: Counter[str] = Counter()
    missing_counts = 0

    for r in passed_records:
        if r.get(status_key) != "ok":
            continue
        main_counts = r.get(main_counts_key)
        fine_counts = r.get(fine_counts_key)
        if not isinstance(main_counts, dict) or not isinstance(fine_counts, dict):
            missing_counts += 1
            continue

        # presence + occurrence for main
        for k, v in main_counts.items():
            try:
                iv = int(v)
            except Exception:
                iv = 0
            if iv <= 0:
                continue
            main_occ[k] += iv
            main_pres[k] += 1

        # presence + occurrence for fine
        for k, v in fine_counts.items():
            try:
                iv = int(v)
            except Exception:
                iv = 0
            if iv <= 0:
                continue
            fine_occ[k] += iv
            fine_pres[k] += 1

    print(f"\n{label} passed/correct GumTree summary:")
    print(f"  n_records: {n}")
    print(f"  gumtree_ok: {ok_records}")
    for k in sorted(statuses.keys()):
        if k == "ok":
            continue
        print(f"  gumtree_{k}: {statuses[k]}")
    if missing_counts:
        print(f"  <missing_category_counts>: {missing_counts}")

    # Paper-style reporting: aggregate primarily by subcategory (types).
    # NOTE: `avg/rec` is computed over *all passed/correct records* (n), not `records=...` for the type,
    # matching the earlier table-style summaries you referenced.
    for m in ordered_mains:
        subs = [k for k in fine_occ.keys() if k.startswith(m + ":")]
        if not subs:
            continue
        # For the main-category line, print the sum of the subcategory "records=..." counts (not a union),
        # matching the paper-style aggregation you want.
        main_records_sum = sum(fine_pres.get(k, 0) for k in subs)
        print(f"{m} {main_records_sum}")
        for k in sorted(subs, key=lambda kk: (-fine_occ[kk], kk)):
            s_occ = fine_occ[k]
            s_pres = fine_pres.get(k, 0)
            s_avg = (s_occ / n) if n else 0.0
            print(f"  {k.split(':', 1)[1].strip()}: records={s_pres}  occ={s_occ}  avg/rec={s_avg:.2f}")


def main() -> None:
    parser = argparse.ArgumentParser(description="Evaluate AdaptAgent results.")
    parser.add_argument(
        "--dataset",
        default=str(DEFAULT_DATASET),
        help="AdaptAgent marked dataset JSON.",
    )
    parser.add_argument(
        "--skip-sim",
        action="store_true",
        help="Deprecated (eval.py no longer recomputes similarity).",
    )
    parser.add_argument(
        "--skip-gumtree",
        action="store_true",
        help="Skip GumTree edit extraction (by default we write GumTree results into the marked dataset).",
    )
    parser.add_argument(
        "--skip-gumtree-failures",
        action="store_true",
        help="Continue when GumTree fails for a record (marks gumtree_status=parse_failed).",
    )
    args = parser.parse_args()

    data_path = Path(args.dataset)
    data = _read_json(data_path)
    # Normalize eval fields *in place* without depending on external test harness folders.
    #
    # - We treat has_tests_repo as authoritative if present in the dataset.
    # - We compute test_outcome from (has_tests_repo, manual_outcome).
    # - We permanently drop adapt_llm_judge_decision from the shipped dataset.
    for rec in data:
        decision = rec.pop("adapt_llm_judge_decision", None)
        has_tests = bool(rec.get("has_tests_repo"))
        rec["has_tests_repo"] = has_tests
        if has_tests:
            # Ensure test_outcome is present; derive from manual_outcome if needed.
            test_outcome = rec.get("test_outcome")
            if test_outcome not in ("Pass", "Fail"):
                manual = rec.get("manual_outcome")
                if manual == "Corr":
                    test_outcome = "Pass"
                elif manual == "Incorr":
                    test_outcome = "Fail"
                elif decision == "Yes":
                    test_outcome = "Pass"
                elif decision == "No":
                    test_outcome = "Fail"
                else:
                    test_outcome = None
            rec["test_outcome"] = test_outcome
            # Requested: remove manual_outcome for has_tests_repo==true entries.
            rec.pop("manual_outcome", None)
        else:
            manual = rec.get("manual_outcome")
            if manual not in ("Corr", "Incorr"):
                if decision == "Yes":
                    manual = "Corr"
                elif decision == "No":
                    manual = "Incorr"
                else:
                    manual = "Corr"
            rec["manual_outcome"] = manual
            rec["test_outcome"] = None

    data_path.write_text(json.dumps(data, indent=2, ensure_ascii=True), encoding="utf-8")

    # GumTree edits: write back into the marked dataset.
    if not args.skip_gumtree:
        gumtree_script = ROOT / "testing" / "gumtree_taxonomy.py"
        if not gumtree_script.exists():
            raise FileNotFoundError(gumtree_script)
        cmd = ["python3", str(gumtree_script), "--dataset", str(data_path), "--in-place"]
        if args.skip_gumtree_failures:
            cmd.append("--skip-failures")
        subprocess.run(cmd, check=True)

    # Reload so we see GumTree updates written by the subprocess.
    data = _read_json(data_path)

    # Print pass/fail rates (test buckets) and manual rates
    adapt_test_total = sum(1 for r in data if r.get("has_tests_repo"))
    adapt_test_pass = sum(1 for r in data if r.get("has_tests_repo") and r.get("test_outcome") == "Pass")
    adapt_test_fail = sum(1 for r in data if r.get("has_tests_repo") and r.get("test_outcome") == "Fail")
    adapt_test_missing = adapt_test_total - adapt_test_pass - adapt_test_fail

    adapt_manual_total = sum(1 for r in data if not r.get("has_tests_repo"))
    adapt_manual_corr = sum(
        1 for r in data if (not r.get("has_tests_repo")) and r.get("manual_outcome") == "Corr"
    )
    adapt_manual_incorr = sum(
        1 for r in data if (not r.get("has_tests_repo")) and r.get("manual_outcome") == "Incorr"
    )
    adapt_manual_missing = adapt_manual_total - adapt_manual_corr - adapt_manual_incorr

    print("AdaptAgent test pass/fail:")
    print(f"  test:Pass {adapt_test_pass}/{adapt_test_total}")
    print(f"  test:Fail {adapt_test_fail}/{adapt_test_total}")
    print(f"  test:Missing {adapt_test_missing}/{adapt_test_total}")
    print(f"  manual:Corr {adapt_manual_corr}/{adapt_manual_total}")
    print(f"  manual:Incorr {adapt_manual_incorr}/{adapt_manual_total}")
    print(f"  manual:Missing {adapt_manual_missing}/{adapt_manual_total}")

    print("\nSimilarity summary:")
    _summarize_similarity(
        data,
        "AdaptAgent",
        "has_tests_repo",
        "manual_outcome",
        "test_outcome",
        "edit_sim",
        "bleu_sim",
        "ast_sim",
    )

    _summarize_gumtree(
        data,
        "AdaptAgent",
        has_tests_key="has_tests_repo",
        manual_key="manual_outcome",
        test_key="test_outcome",
        status_key="gumtree_status",
        cats_key="gumtree_main_categories",
    )
    _summarize_pass_correct_gumtree(
        data,
        "AdaptAgent",
        has_tests_key="has_tests_repo",
        manual_key="manual_outcome",
        test_key="test_outcome",
    )


if __name__ == "__main__":
    main()
