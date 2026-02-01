# Code Adaptation Pipeline

This folder contains:
- The main adaptation pipeline (`run_adaptation_dataset.py` / `adapt_agent.py`)
- The latest dataset + generated artifacts under `data/`
- Evaluation (`eval.py`) and helper scripts under `testing/`

The primary artifact is:
- `data/adaptations_with_snapshots_with_intent_and_results_marked.json` (952 records)

## Quick Start

From this directory:

### 0) Configure the LLM client (Azure OpenAI)

`util.llm_gpt4o()` reads Azure OpenAI config from env vars:

```bash
export AZURE_OPENAI_ENDPOINT="https://<your-resource>.openai.azure.com/"
export AZURE_OPENAI_API_KEY="<your-key>"
export AZURE_OPENAI_DEPLOYMENT="gpt-4o"              # optional (your deployment name)
export AZURE_OPENAI_API_VERSION="<your api version>" # optional
```

### 1) Run AdaptAgent on the dataset (optional)

If the dataset already contains `adapt_agent_*` fields, you can skip this step.

```bash
python3 run_adaptation_dataset.py \
  --input data/adaptations_with_snapshots_with_intent_and_results_marked.json \
  --output data/adaptations_with_snapshots_with_intent_and_results_marked.json \
  --dataset-root data/adaptation-dataset
```

Per-record artifacts are written under `data/adaptation-dataset/so-*/`, including:
- `adapt_agent_result_<gh_id>.java` (generated function-only output)
- `adapt_agent_response_<gh_id>.json` (raw model response in JSON form)

### 2) Run evaluation

```bash
python3 eval.py --dataset data/adaptations_with_snapshots_with_intent_and_results_marked.json
```

Notes:
- `eval.py` updates the dataset **in-place** (only adds/updates evaluation-related fields).
- GumTree taxonomy extraction is performed via `testing/gumtree_taxonomy.py`.
  - Skip taxonomy: `python3 eval.py --skip-gumtree`
  - Continue on taxonomy failures: `python3 eval.py --skip-failures`

## Docker environment (placeholder)

Some builds/tests were originally run inside Docker for repeatability (consistent JDK/Gradle/Android SDK).

If you maintain a Docker setup for this project, document it here (e.g., `Dockerfile` + `docker-compose.yml`)
and ensure the repo is mounted into the container at a consistent workspace path (many scripts assume `/work`).

This repository intentionally does not assume any specific container/service name.

## Dataset: what’s stored in `*_marked.json`

Each record corresponds to a StackOverflow snippet and a GitHub target location.

### Identifiers
- `so_id`, `so_key`
- `gh_id`, `gh_index`
- `gh_url` (includes commit SHA + file path + line range)

### Inputs / context
- `so_snippet`
- `gh_snippet` (developer ground-truth)
- `file_level_code`
- `file_level_code_without_target`
- `file_context` (structured context: imports/callers/callees/etc.)
- `adaptation_intent` (if present)
- `adaptation_types` (if present)

### AdaptAgent outputs
- `adapt_agent_status`: `success|failed|skipped`
- `adapt_agent_failure_reason` (if failed/skipped)
- `adapt_agent_code` (generated function-only Java code)
- `adapt_agent_explanation` (model explanation string)
- `adapt_agent_response_raw` (raw JSON returned by the model)

### Test-suite availability + outcomes
- `has_tests_repo` (boolean)
- `available_repo_id` (repo identifier used when marking test-suite repos)

For `has_tests_repo == true`:
- `test_outcome`: `Pass|Fail`

For `has_tests_repo == false`:
- `manual_outcome`: `Corr|Incorr`

### Similarity metrics
- `edit_sim`
- `bleu_sim`
- `ast_sim`

### GumTree taxonomy output (if computed)
- `gumtree_status` (`ok|parse_failed|...`)
- `gumtree_action_counts`
- `gumtree_main_categories`, `gumtree_main_category_counts`
- `gumtree_fine_categories`, `gumtree_fine_category_counts`

## Environment variables

- `CODE_ADAPT_RESULTS_ROOT`: optional override for `results/` output location (see `paths.py`).
