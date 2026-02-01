# `fse_2026`: LLMAdapt pipeline + dataset + evaluation

This folder contains the **LLMAdapt** pipeline (AdaptAgent), the **frozen GitHub snapshots** at specific SHAs, and the **latest dataset/results** used for evaluation.

The primary artifact is:

- `data/adaptations_with_snapshots_with_intent_and_results_marked.json` (952 records)

## Quick Start

From `code_adaptation/fse_2026/`:

### 0) Configure GPT-5 (Azure OpenAI)

`util.llm_gpt5()` reads Azure OpenAI config from env vars:

```bash
export AZURE_OPENAI_ENDPOINT="https://<your-resource>.openai.azure.com/"
export AZURE_OPENAI_API_KEY="<your-key>"
export AZURE_OPENAI_DEPLOYMENT="gpt-5.2-chat-2"        # optional
export AZURE_OPENAI_API_VERSION="2024-12-01-preview"   # optional
```

### 1) Run AdaptAgent on the dataset (optional)

If you already have `adapt_agent_*` fields filled in the marked dataset, you can skip this.

```bash
python3 run_adaptation_dataset.py \
  --input data/adaptations_with_snapshots_with_intent_and_results_marked.json \
  --output data/adaptations_with_snapshots_with_intent_and_results_marked.json \
  --dataset-root data/adaptation-dataset
```

This also writes per-record artifacts into `data/adaptation-dataset/so-*/`:

- `adapt_agent_result_<gh_id>.java` (generated function-only output)
- `adapt_agent_response_<gh_id>.json` (raw LLM JSON response)

### 2) Run evaluation

```bash
python3 eval.py --dataset data/adaptations_with_snapshots_with_intent_and_results_marked.json
```

Notes:
- `eval.py` **updates the dataset in-place** (normalizes `has_tests_repo`, `test_outcome`, and `manual_outcome`).
- By default it also runs GumTree taxonomy extraction via `testing/gumtree_taxonomy.py`.
  - To skip GumTree: `python3 eval.py --skip-gumtree`
  - To continue on GumTree parse failures: `python3 eval.py --skip-gumtree-failures`

## Docker environment (optional)

This project was originally designed to run builds/tests inside a Docker container for
repeatability (consistent JDK/Gradle/Android SDK).

You can recreate a compatible environment using the included `Dockerfile` and `docker-compose.yml`:

```bash
docker compose build
docker compose run --rm fse2026 bash
```

Notes:
- The compose service name is `fse2026` (see `docker-compose.yml`).
- The repository is mounted into the container at `/work`.
- If you need a fixed container name (e.g., older scripts referenced `fse2026-dev`),
  add `container_name: fse2026-dev` under the service in `docker-compose.yml`.

## Dataset: what’s stored in `*_marked.json`

Each record is keyed by a StackOverflow snippet + a GitHub target location.

### Identifiers
- `so_id`, `so_key`
- `gh_id`, `gh_index`
- `gh_url` (includes the commit SHA + file path + line range)

### Inputs / context (grounded)
- `so_snippet` (StackOverflow code snippet)
- `gh_snippet` (developer ground-truth function/snippet in GitHub)
- `file_level_code` (file-level GitHub code)
- `file_level_code_without_target` (file-level code with the target removed/replaced by placeholder)
- `file_context` (precomputed structured context: callers/callees/imports/etc.)
- `adaptation_intent` (human/LLM-produced intent text)
- `adaptation_types` (if present; dataset metadata)

### AdaptAgent outputs
- `adapt_agent_status`: `success|failed|skipped`
- `adapt_agent_failure_reason` (if failed/skipped)
- `adapt_agent_code` (generated function-only Java code)
- `adapt_agent_explanation` (LLM explanation string)
- `adapt_agent_response_raw` (raw JSON returned by the model)
- `adapt_agent_integration_check` (syntactic integration check result)

### Test-suite availability + outcomes
- `has_tests_repo` (boolean; whether this GH repo is in the “available test-suite” set)
- `available_repo_id` (repo identifier used when marking test-suite repos)

For `has_tests_repo == true`:
- `test_outcome`: `Pass|Fail`
- `manual_outcome` is intentionally **removed** (test-suite bucket uses `test_outcome` only)

For `has_tests_repo == false`:
- `manual_outcome`: `Corr|Incorr`
- `test_outcome` is present but `null`

### Similarity metrics
Precomputed per-record similarity values:
- `edit_sim` (string-edit similarity)
- `bleu_sim` (token-level BLEU)
- `ast_sim` (AST node match ratio)

### GumTree taxonomy output
Populated by `testing/gumtree_taxonomy.py`:
- `gumtree_status` (`ok|parse_failed|...`)
- `gumtree_action_counts` (raw GumTree action tallies)
- `gumtree_main_categories`, `gumtree_main_category_counts`
- `gumtree_fine_categories`, `gumtree_fine_category_counts`

## File/Folder guide (excluding snapshot contents)

### Top-level scripts
- `run_adaptation_dataset.py`: batch runner that fills `adapt_agent_*` fields and writes `data/adaptation-dataset/so-*/adapt_agent_result_*.java`.
- `adapt_agent.py`: core AdaptAgent pipeline (summarize → plan → adapt → (optional) verify).
- `eval.py`: evaluation driver; normalizes outcomes, optionally runs GumTree extraction, prints summaries.
- `util.py`: shared utilities (LLM client wrapper, StackExchange API helpers, call graph, etc.).
- `prompt.py`: prompt templates used by the pipeline.
- `paths.py`: path helpers used across modules.
- `context_miner.py`: context mining helper (used for dataset context).
- `file_context.py`: file-level context extraction utilities (imports/callers/callees/etc.).
- `file_call_graph.py`: call-graph helper (legacy/optional; not required for eval).
- `joern_runner.py`: helper wrapper around the local Joern CLI (optional; not required by the core pipeline).

### Docker
- `Dockerfile`, `docker-compose.yml`: environment setup for building/testing repos (Android SDK, Gradle, etc.).
- `android-sdk-min/`: minimized Android SDK(s) used by some Android repos.

### Data
- `data/adaptations_with_snapshots_with_intent_and_results_marked.json`: **main dataset** for reproduction/eval.
- `data/adaptations.csv`: CSV export of the dataset (metadata).
- `data/adaptation-dataset/`: per-record artifacts (snippets, “without-target” files, AdaptAgent outputs).

### Evaluation helpers
`testing/`:
- `gumtree_taxonomy.py`: runs GumTree diff and maps edits to the 6 main categories + subcategories; can update the dataset in-place.
- `similarity_metrics.py`: utilities for computing/storing similarity metrics.
- `adaptation_taxonomy.py`: taxonomy constants/utilities (6 categories + subcategories).

### Results / logs
`results/`:
- `repo_test_scan.json`: scan summary of repo test harness availability.
- `github_clone_summary.json`, `github_clone_current_summary.json`: clone/snapshot summaries.
- `adaptation_dataset_*.txt`: bookkeeping lists for dataset artifacts.

## Notes on StackOverflow “full post” fetching

The current AdaptAgent run uses `record["adaptation_intent"]` from the dataset and does **not** fetch StackOverflow posts at runtime.

If you ever need to reconstruct the full post, `util.extract_so_post_api(so_id)` can fetch:
- question HTML
- accepted answer HTML
- tags
