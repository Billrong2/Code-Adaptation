from __future__ import annotations

import argparse
import json
from pathlib import Path
from typing import Any, Dict, List

from tqdm import tqdm

from file_context import build_file_context_summary


def _load_json(path: Path) -> List[Dict[str, Any]]:
    with path.open("r", encoding="utf-8") as handle:
        data = json.load(handle)
    if not isinstance(data, list):
        raise ValueError(f"Expected a list in {path}, got {type(data).__name__}")
    return data


def _write_json(path: Path, data: List[Dict[str, Any]]) -> None:
    temp_path = path.with_suffix(path.suffix + ".tmp")
    with temp_path.open("w", encoding="utf-8") as handle:
        json.dump(data, handle, indent=2, ensure_ascii=False)
    temp_path.replace(path)


def _build_context(item: Dict[str, Any], max_hops: int) -> Dict[str, Any]:
    file_code = item.get("file_level_code_without_target") or ""
    gh_snippet = item.get("gh_snippet") or ""
    if not file_code or not gh_snippet:
        return {
            "error": "missing file_level_code_without_target or gh_snippet",
        }
    try:
        return build_file_context_summary(file_code, gh_snippet, max_hops=max_hops)
    except Exception as exc:
        return {
            "error": f"{type(exc).__name__}: {exc}",
        }


def main() -> None:
    parser = argparse.ArgumentParser(
        description="Populate file-level context for adaptations dataset."
    )
    parser.add_argument(
        "--max-hops",
        type=int,
        default=1,
        help="Max call-graph hops to include for context.",
    )
    args = parser.parse_args()

    input_path = Path("data/adaptations_with_snapshots_with_intent.json")
    output_path = input_path
    data = _load_json(input_path)

    for idx in tqdm(range(len(data)), desc="Context mining"):
        item = data[idx]
        item["file_context"] = _build_context(item, args.max_hops)

    _write_json(output_path, data)


if __name__ == "__main__":
    main()
