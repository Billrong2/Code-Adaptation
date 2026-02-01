from pathlib import Path
import os

ROOT = Path(__file__).resolve().parent

# Results root (override with env var if needed)
RESULTS_ROOT = Path(os.getenv("FSE_RESULTS_ROOT", str(ROOT / "results")))

# Current (preferred) results layout
ADAPTATION_DIR = RESULTS_ROOT / "adaptation"
AGENTLESS_DIR = RESULTS_ROOT / "agentless"
PDG_DIR = RESULTS_ROOT / "pdg"
EVAL_DIR = RESULTS_ROOT / "eval"
LOGS_DIR = RESULTS_ROOT / "logs"

# Legacy layouts (kept for compatibility)
LEGACY_ADAPTATION_DIR = ROOT / "adaptation_result"
LEGACY_AGENTLESS_DIR = ROOT / "agentless_res"

# Dataset files
DATASET_PATH = ROOT / "dataset.json"
DATASET_WITH_INTENT_PATH = ROOT / "dataset_with_intent.json"
DATASET_AGENTLESS_PATH = ROOT / "dataset_agentless.json"
TAGGED_DATASET_PREFIX = ROOT / "code_adaptation_dataset_tag_"


def ensure_results_dirs() -> None:
    for path in (RESULTS_ROOT, ADAPTATION_DIR, AGENTLESS_DIR, PDG_DIR, EVAL_DIR, LOGS_DIR):
        path.mkdir(parents=True, exist_ok=True)


def resolve_adaptation_dir() -> Path:
    if ADAPTATION_DIR.exists() and any(ADAPTATION_DIR.iterdir()):
        return ADAPTATION_DIR
    if LEGACY_ADAPTATION_DIR.exists():
        return LEGACY_ADAPTATION_DIR
    return ADAPTATION_DIR


def resolve_agentless_dir() -> Path:
    if AGENTLESS_DIR.exists() and any(AGENTLESS_DIR.iterdir()):
        return AGENTLESS_DIR
    if LEGACY_AGENTLESS_DIR.exists():
        return LEGACY_AGENTLESS_DIR
    return AGENTLESS_DIR


def list_case_ids() -> list[str]:
    base = resolve_adaptation_dir()
    if not base.exists():
        return []
    return sorted([p.name for p in base.iterdir() if p.is_dir()])


def case_output_dir(so_id: str, variant: str) -> Path:
    """
    variant:
      - "adapted" or "developer_edit" -> under adaptation root
      - "agentless" -> under agentless root (always "adapted")
    """
    if variant == "agentless":
        return resolve_agentless_dir() / so_id / "adapted"
    return resolve_adaptation_dir() / so_id / variant


def case_root_dir(so_id: str) -> Path:
    return resolve_adaptation_dir() / so_id


def eval_output_path(model: str) -> Path:
    return EVAL_DIR / f"pdg_overlap_{model}.json"


def tagged_dataset_path(tag: str) -> Path:
    return Path(f"{TAGGED_DATASET_PREFIX}{tag}.json")
