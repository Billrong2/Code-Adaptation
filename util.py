import hashlib
import json
import os
import shutil
import subprocess
import tempfile
import time
from pathlib import Path

import javalang
import requests

from joern_runner import JoernRunner
def llm_gpt5(
    user_prompt: str,
    system_prompt: str = "",
    top_p: float = 1.0,
    presence_penalty: float = 0.0,
    frequency_penalty: float = 0.0,
) -> str:
    from openai import AzureOpenAI

    azure_endpoint = os.environ.get("AZURE_OPENAI_ENDPOINT")
    api_key = os.environ.get("AZURE_OPENAI_API_KEY")
    api_version = os.environ.get("AZURE_OPENAI_API_VERSION", "2024-12-01-preview")
    deployment = os.environ.get("AZURE_OPENAI_DEPLOYMENT", "gpt-5.2-chat-2")

    if not azure_endpoint or not api_key:
        raise RuntimeError(
            "Missing Azure OpenAI configuration. Set AZURE_OPENAI_ENDPOINT and "
            "AZURE_OPENAI_API_KEY (optionally AZURE_OPENAI_API_VERSION and "
            "AZURE_OPENAI_DEPLOYMENT)."
        )

    client = AzureOpenAI(api_version=api_version, azure_endpoint=azure_endpoint, api_key=api_key)
    response = client.chat.completions.create(
    messages=[
        {
            "role": "system",
            "content": system_prompt,
        },
        {
            "role": "user",
            "content": user_prompt,
        }
    ],
    max_completion_tokens =16384,
    model=deployment
    )
    print(response.choices[0].message.content)
    return response.choices[0].message.content
class Joern:
    def __init__(self, joern_path):
        self._runner = JoernRunner(Path(joern_path))

    def generate_prolog(self, code, destination=None):
        if code.strip() == "":
            print("Empty File!")
            return (
                "Joern Failed to parse the code",
                "Joern Failed to parse the code",
                "Joern Failed to parse the code",
                "Empty file",
                "Empty file",
            )
        dest_path = Path(destination) if destination is not None else None
        self._runner.export(code, dest_path)


def download_github_file_by_url(url):
    """
    Downloads a raw file from a GitHub repository using its raw URL.

    :param url: The raw GitHub file URL
    :return: The content of the file as a string
    """
    try:
        response = requests.get(url)
        response.raise_for_status()  # Raise an error for bad status codes
        return response.text
    except requests.exceptions.RequestException as e:
        print(f"Error downloading the file: {e}")
        return None
def find_bug_tag_in_SO_POST(SO_url):
    question_text, answer_text, tags = extract_so_post(SO_url)
    if "bug" in str(tags).lower():
        return True
    if "bug" in str(question_text).lower():
        return True
    else:
        return False
def read_csv(file_name):
    # Initialize an empty array
    data_array = []
    import csv

    # Open and read the CSV file
    with open(file_name, mode='r') as file:
        csv_reader = csv.reader(file)
        for row in csv_reader:
            data_array.append(row)
    return data_array    
## given a SO post url, it will return question text and answer text
def split_lines_to_list(graph):
    with open('temp.txt', 'w') as infile:
        infile.write(str(graph))
    with open("temp.txt", 'r') as read_file:
        index = read_file.readlines()
    # subprocess.run(f"rm -rf /temp.txt", shell=True)
    return index
API = "https://api.stackexchange.com/2.3"
COMMON_PARAMS = {"site": "stackoverflow"}

def extract_so_post_api(so_id: int):
    # First: detect the post type (question or answer)
    r = requests.get(f"{API}/posts/{so_id}", params=COMMON_PARAMS, timeout=20)
    r.raise_for_status()
    items = r.json().get("items", [])
    if not items:
        raise ValueError(f"No post found for id {so_id}")
    post = items[0]
    post_type = post["post_type"]

    if post_type == "question":
        qr = requests.get(
            f"{API}/questions/{so_id}",
            params={**COMMON_PARAMS, "filter": "withbody"},
            timeout=20,
        )
        qr.raise_for_status()
        q = qr.json()["items"][0]
        question_html = q["body"]
        tags_csv = ", ".join(q.get("tags", []))

        # Fetch accepted answer if present; else fetch all answers
        accepted_id = q.get("accepted_answer_id")
        if accepted_id:
            ar = requests.get(
                f"{API}/answers/{accepted_id}",
                params={**COMMON_PARAMS, "filter": "withbody"},
                timeout=20,
            )
            ar.raise_for_status()
            answer_html = ar.json()["items"][0]["body"]
        else:
            ar = requests.get(
                f"{API}/questions/{so_id}/answers",
                params={**COMMON_PARAMS, "filter": "withbody", "sort": "votes", "order": "desc"},
                timeout=20,
            )
            ar.raise_for_status()
            answers = ar.json().get("items", [])
            answer_html = "\n-----\n".join([a["body"] for a in answers]) if answers else ""

        return question_html, answer_html, tags_csv

    else:  # answer
        ar = requests.get(
            f"{API}/answers/{so_id}",
            params={**COMMON_PARAMS, "filter": "withbody"},
            timeout=20,
        )
        ar.raise_for_status()
        a = ar.json()["items"][0]
        answer_html = a["body"]
        qid = a["question_id"]

        qr = requests.get(
            f"{API}/questions/{qid}",
            params={**COMMON_PARAMS, "filter": "withbody"},
            timeout=20,
        )
        qr.raise_for_status()
        q = qr.json()["items"][0]
        question_html = q["body"]
        tags_csv = ", ".join(q.get("tags", []))
        return question_html, answer_html, tags_csv
def remove_comments(string):
    import regex as re
    pattern = r"(\".*?\"|\'.*?\')|(/\*.*?\*/|//[^\r\n]*$)"
    # first group captures quoted strings (double or single)
    # second group captures comments (//single-line or /* multi-line */)
    regex = re.compile(pattern, re.MULTILINE|re.DOTALL)
    def _replacer(match):
        # if the 2nd group (capturing comments) is not None,
        # it means we have captured a non-quoted (real) comment string.
        if match.group(2) is not None:
            return "" # so we will return empty to remove the comment
        else: # otherwise, we will return the 1st group
            return match.group(1) # captured quoted-string
    return regex.sub(_replacer, string)
def fetch_stackexchange_data():
    '''
    this post is used for extracting SO post using Stack Exchange API call
    but since we already have Stack Exchange Datadump from online source:
    e.g. https://archive.org/download/stack-exchange-data-dump-2023-09-12
    we no longer need this one.
    '''
    import requests
    url = "https://api.stackexchange.com/2.3/questions"
    params = {
        "site": "stackoverflow",
        "tagged": "python",
        "pagesize": 10,
        "key": "rl_upY6N98yoTLyYi9KGdEiTN52b"
    }
    response = requests.get(url, params=params)
    if response.status_code == 200:
        return response.json()
    else:
        print(f"Error: {response.status_code}")
        return None

class call_graph():
    def __init__(self):
        pass
    def build_call_graph_from_code(self,code_string):
        """
        Parses Java code (as a string), identifies method calls,
        and returns a list of call edges.
        Each edge is a tuple: (caller_class, caller_method, callee_name).

        Caveat: This is a naive approach; it doesn't resolve the actual declaring class for 'callee_name'.
        """
        call_graph = []

        try:
            tree = javalang.parse.parse(code_string)
        except (javalang.parser.JavaSyntaxError, TypeError) as e:
            print(f"Error parsing code string: {e}")
            return call_graph  # Return empty if parsing fails

        # Determine a top-level class name if available
        package_name = tree.package.name if tree.package else "default"
        caller_class = f"{package_name}.UnknownType"
        if tree.types and hasattr(tree.types[0], 'name'):
            top_level_class_name = tree.types[0].name
            caller_class = f"{package_name}.{top_level_class_name}"

        # Find all MethodDeclaration nodes
        for _, method_decl in tree.filter(javalang.tree.MethodDeclaration):
            caller_method_name = method_decl.name

            # Search within the entire method declaration node for MethodInvocation
            for _, invocation in method_decl.filter(javalang.tree.MethodInvocation):
                callee_name = invocation.member  # e.g., 'println'
                call_graph.append((caller_class, caller_method_name, callee_name))

        return call_graph

    def summarize_call_graph(self,call_graph):
        """
        call_graph is a list of tuples: (caller_class, caller_method, callee_name)

        Example:
        [
        ("com.example.HelloWorld", "greet", "println"),
        ("com.example.HelloWorld", "greet", "sayBye"),
        ("com.example.HelloWorld", "sayBye", "println")
        ]
        """
        from collections import defaultdict
        # We'll group by class, then by method
        class_map = defaultdict(lambda: defaultdict(set))

        for caller_class, caller_method, callee_name in call_graph:
            class_map[caller_class][caller_method].add(callee_name)

        # Print the result in a readable way
        returns = []
        for class_name, methods in class_map.items():
            # Optionally strip out the package name if you want "HelloWorld" only
            simple_class_name = class_name.split('.')[-1]
            # print(f"Under class {simple_class_name}:")
            returns.append(f"Under class {simple_class_name}():")
            for method_name, callees in methods.items():
                callees_str = "(), ".join(sorted(callees))
                returns.append(f"  function {method_name}() calls: {callees_str}()")
                # print(f"  function {method_name} calls: {callees_str}")
        return "\n".join(returns)
    def get_call_graph(self, code_string):
        """
        Parses Java code (as a string), identifies method calls,
        and returns a list of call edges.
        Each edge is a tuple: (caller_class, caller_method, callee_name).

        Caveat: This is a naive approach; it doesn't resolve the actual declaring class for 'callee_name'.
        """
        graph = self.build_call_graph_from_code(code_string)
        # print(self.summarize_call_graph(graph))
        return self.summarize_call_graph(graph)

class get_repo_list():
    '''
    this class is for parsing the entire github repositories, need slightly modify to date based 
    parsing.
    '''
    def __init__(self):
        pass
    def fetch_repositories(self, access_token=None, start_time="2020-01-01T00:00:00Z", output_dir="repositories"):
        headers = {}
        if access_token:
            headers["Authorization"] = f"token {access_token}"

        base_url = "https://api.github.com/search/repositories"
        current_time = start_time
        os.makedirs(output_dir, exist_ok=True)

        seen_repositories = set()  # Track repositories already processed to avoid duplicates

        while True:
            all_repositories = []  # Collect all repositories for this query
            page = 1  # Start pagination for this query

            while True:  # Pagination loop
                params = {
                    "q": f"pushed:>={current_time}",  # Inclusive filter to prevent missing repositories
                    "sort": "updated",
                    "order": "asc",  # Ascending order ensures we fetch the oldest updated first
                    "per_page": 100,
                    "page": page
                }

                print(f"Fetching repositories updated after {current_time}, page {page}...")
                response = requests.get(base_url, headers=headers, params=params)

                # Handle rate limit
                if response.status_code == 403:
                    reset_time = int(response.headers.get("X-RateLimit-Reset", time.time() + 60))
                    wait_time = max(0, reset_time - time.time())
                    print(f"Rate limit exceeded. Waiting for {wait_time:.2f} seconds...")
                    time.sleep(wait_time)
                    continue

                if response.status_code != 200:
                    print(f"Error: {response.status_code} - {response.text}")
                    break

                data = response.json()
                items = data.get("items", [])
                if not items:
                    print(f"No more repositories to fetch for page {page}.")
                    break

                # Add items to the list, skipping already processed repositories
                for item in items:
                    if item["full_name"] in seen_repositories:
                        continue  # Skip already processed repositories
                    seen_repositories.add(item["full_name"])
                    all_repositories.append({
                        "name": item["full_name"],
                        "stars": item["stargazers_count"],
                        "updated_at": item["updated_at"],
                        "description": item.get("description", ""),
                    })

                # If less than 100 results are returned, we are at the end of this query
                if len(items) < 100:
                    break

                page += 1
                time.sleep(1)  # Avoid hitting rate limits

            if not all_repositories:
                print(f"No more repositories to fetch starting from {current_time}.")
                break

            # Save all repositories for this query to a CSV file
            output_file = os.path.join(output_dir, f"repositories_from_{current_time.replace(':', '-')}.csv")
            import pandas as pd
            df = pd.DataFrame(all_repositories)
            df.to_csv(output_file, index=False)
            print(f"Saved {len(all_repositories)} repositories to '{output_file}'.")

            # Update the current time to the `updated_at` of the last repository
            last_updated_at = all_repositories[-1]["updated_at"]
            if current_time == last_updated_at:
                print(f"No progress made: current_time ({current_time}) matches last_updated_at. Stopping.")
                break
            current_time = last_updated_at

            # Wait briefly to avoid hitting API limits
            time.sleep(1)
def check_data_QApair(data_path = ""):
    import json
    import tqdm

    with open(data_path, 'r') as infile:
        SO_POST = json.load(infile)
    QA_pair = []
    post_lookup = {post['Id']: post['Body'] for post in SO_POST}
    for item in tqdm.tqdm(SO_POST, desc="Processing posts", unit="post"):
        post_id = item["Id"]
        PostTypeId = item["PostTypeId"]
        if PostTypeId != "1":
            continue
        Title = item["Title"]
        Tags = item["Tags"]
        if not item.get("AcceptedAnswerId"):
            continue
        else:
            AcceptedAnswerId = item["AcceptedAnswerId"]
        if AcceptedAnswerId:
            AccpetedAnswer = post_lookup.get(AcceptedAnswerId, None)
            Answer_code = extract_code_snippets(AccpetedAnswer)   
        def detect_first_laugnage(tags):
            language_patterns = {
                "java": ["java", "spring", ".java"],
                "c++": ["c++", "cpp", ".cpp", ".cc", "boost"],
                "python": ["python", "django", "flask", ".py", "pandas", "numpy", "tensorflow"],
                "javascript": ["javascript", "nodejs", "react", "vue", "angular", ".js"],
                "c#": ["c#", ".cs", "dotnet", "asp.net", "blazor"],
                "ruby": ["ruby", "rails", ".rb"],
                "php": ["php", "laravel", "symfony", "wordpress"],
                "go": ["go", "golang", ".go"],
                "rust": ["rust", ".rs", "cargo"],
                "swift": ["swift", ".swift", "ios", "xcode"],
                "kotlin": ["kotlin", "android", ".kt"],
                "r": ["r", ".r", "ggplot2", "tidyverse"],
                "perl": ["perl", ".pl"],
                "scala": ["scala", ".scala", "play"],
                "typescript": ["typescript", ".ts"],
                "shell": ["bash", "shell", ".sh", "zsh"],
                "html/css": ["html", "css", ".html", ".css", "bootstrap", "tailwind"],
                "sql": ["sql", "postgresql", "mysql", "sqlite", "mssql"],
                "matlab": ["matlab", ".m", "simulink"],
            }
            for tag in tags:
                for lang, patterns in language_patterns.items():
                    if any(pattern.lower() in tag.lower() for pattern in patterns):
                        return lang
            return None
        language = detect_first_laugnage(Tags)
        QA_pair.append({
            "post_id": post_id,
            "Title": Title,
            "AcceptedAnswerId":AcceptedAnswerId, 
            "question_body" : extract_code_snippets(item["Body"]),
            "tags": Tags,
            "AcceptedAnswerBody": AccpetedAnswer,
            "Answer_code": Answer_code,
            "language": language
            
        })
        if language:
            with open(f"Dataset/SO-Code/{post_id}.{language}", 'w') as outfile:
                outfile.write('\n'.join(Answer_code)) 
    import shutil
    shutil.make_archive("SO-Code", 'zip', "Dataset/SO-Code")

    with open("QA_pair.json",'w') as outfile:
        json.dump(QA_pair, outfile)
    # post_lookup = {post['Id']: post['Body'] for post in SO_POST}
def get_answer_post(data_path = ""):
    import json
    from tqdm import tqdm
    with open(data_path, 'r') as infile:
        SO_POST = json.load(infile)
    post_lookup = {post['Id']: post['Body'] for post in SO_POST}
    with open("temp.json",'r') as infile:
        only_question = json.load(infile)
        for item in tqdm(only_question, desc="Processing posts", unit="post"):
            AcceptedAnswerId = item.get('AcceptedAnswerId')
            if AcceptedAnswerId:
                item["AcceptedAnswerBody"] = post_lookup.get(AcceptedAnswerId, None)
    with open("QA_pair.json",'w') as outfile:
        json.dump(only_question, outfile)
def output_to_file(data_path = "QA_pair.json"):
    """
    This function is used to output the QA_pair to a file, and make it ready for SourcererCC.
    """    
    import json
    import os
    from tqdm import tqdm
    with open(data_path, 'r') as infile:
        QA_pair = json.load(infile)
    project_list  = []
    for item in tqdm(QA_pair, desc="Processing files", unit="files"):
        post_id = item["post_id"]
        Title = item["Title"]
        question_body = item["question_body"]
        AcceptedAnswerId = item["AcceptedAnswerId"]
        Answer_code = extract_code_snippets(item["AcceptedAnswerBody"])    
        with open(f"Dataset/SO-Code/{post_id}.java", 'w') as outfile:
            outfile.write('\n'.join(Answer_code)) 
        project_list.append(f"Dataset/SO-Code/{post_id}.java")
    with open("Dataset/project_list.txt",'w') as outfile:
        outfile.write('\n'.join(project_list))
def extract_code_snippets(body):
    # Decode HTML entities
    import html
    decoded_body = html.unescape(body)
    # Extract code snippets
    code_snippets = []
    start = 0
    while "<code>" in decoded_body[start:]:
        start_idx = decoded_body.find("<code>", start) + len("<code>")
        end_idx = decoded_body.find("</code>", start_idx)
        if end_idx != -1:
            code_snippets.append(decoded_body[start_idx:end_idx].strip())
            start = end_idx + len("</code>")
        else:
            break
    return code_snippets

def extract_GPT_answer(string):

    try:
        temp = string.split("```")[1]
    except:
        return string
    string = split_lines_to_list(string)
    inside_block = False
    extracted_lines = []
    for line in string:
        if line.startswith("```"):  # Toggle the extraction state
            if inside_block:
                break  # Stop at the second ```
            inside_block = True
            continue  # Skip the ``` line itself
        if inside_block:
            extracted_lines.append(line)
    return "".join(extracted_lines)
class eval():
    def __init__(self):
        pass
    def levenshtein_distance(self,s1: str, s2: str) -> float:
        """Computes Levenshtein distance between two code strings.
        Reference:
        https://stackoverflow.com/questions/2460177/edit-distance-in-python
        Args:
            s1: First code string
            s2: Second code string
        Returns:
            The Levenshtein distance between the two code strings.
        """
        if len(s1) > len(s2):
            s1, s2 = s2, s1
        distances = range(len(s1) + 1)
        for i2, c2 in enumerate(s2):
            distances_ = [i2+1]
            for i1, c1 in enumerate(s1):
                if c1 == c2:
                    distances_.append(distances[i1])
                else:
                    distances_.append(1 + min((distances[i1], distances[i1 + 1], distances_[-1])))
            distances = distances_
        return distances[-1]  

def apply_patch_to_code(patch_content: str, java_code: str) -> str:
    """
    Applies a unidiff patch (provided as a string) to Java code (also a string)
    using a temporary workspace. Returns the patched Java code.
    """
    workspace_dir = "./workplace"
    # Reset the workspace
    if os.path.exists(workspace_dir):
        shutil.rmtree(workspace_dir)
    os.makedirs(workspace_dir)
    
    # Write the Java code to a fixed file name
    workspace_java_file = os.path.join(workspace_dir, "MyJavaFile.java")
    with open(workspace_java_file, "w", encoding="utf-8") as f:
        f.write(java_code)
    
    try:
        # Load the patch
        patch_lines = patch_content.splitlines(keepends=True)
        from unidiff import PatchSet
        patch_set = PatchSet(patch_lines)
        
        # Read original lines from the file
        with open(workspace_java_file, "r", encoding="utf-8") as f:
            original_lines = f.readlines()
        
        # For simplicity, assume one file in the patch:
        patched_lines = apply_patch_hunks(original_lines, patch_set[0])
        
        # Write the patched content back (optional)
        with open(workspace_java_file, "w", encoding="utf-8") as f:
            f.writelines(patched_lines)
        
        patched_code = "".join(patched_lines)
        print("✅ Patch successfully applied to Java code.")
        shutil.rmtree(workspace_dir)
        return patched_code
    except Exception as e:
        print(f"❌ Error applying patch: {e}")
        return None
    finally:
        if os.path.exists(workspace_dir):
            shutil.rmtree(workspace_dir)

def apply_patch_hunks(original_lines, patched_file):
    """
    Applies all hunks from a PatchedFile object to the original lines.
    Uses a relaxed context check (ignores leading/trailing whitespace).
    Returns the modified list of lines.
    """
    patched_lines = original_lines[:]  # work on a copy
    total_offset = 0

    for hunk in patched_file:
        start_index = hunk.source_start - 1 + total_offset
        new_hunk_lines = []
        for hunk_line in hunk:
            if hunk_line.is_removed:
                # Do not add removed lines
                continue
            elif hunk_line.is_added:
                new_hunk_lines.append(hunk_line.value)
            else:
                # For context lines, compare after stripping whitespace
                current_index = start_index + len(new_hunk_lines)
                expected = patched_lines[current_index].rstrip("\n")
                actual = hunk_line.value.rstrip("\n")
                if expected.strip() != actual.strip():
                    raise ValueError(f"Context line mismatch: expected '{expected.strip()}', found '{actual.strip()}'")
                new_hunk_lines.append(hunk_line.value)
        # Replace the block in patched_lines with new_hunk_lines
        patched_lines[start_index:start_index + hunk.source_length] = new_hunk_lines
        total_offset += len(new_hunk_lines) - hunk.source_length

    return patched_lines

def read_csv_file_to_dictionary(file_path = "",
                                output_path = "dataset.json"):
    """
    file_path: str - The path to the CSV file to read. end with .csv
    this is for first step smoke test.
    """
    import json
    tsv_file = file_path # Change this to your actual file path
    # Read the TSV file (tab-separated)
    import pandas as pd
    df = pd.read_csv(tsv_file, sep="\t")

    # Display the first few rows
    # print(df.head())

    #Convert into json format
    json_data = df.to_dict(orient="records")
    with open(output_path,'w')as outfile:
        json.dump(json_data,outfile)
def filter_example_stack_based_on_tag(tag = "Handle_New_Exception",json_output_path = f"code_adaptation_dataset_tag_", output_to_json = True, output_to_file = False):
    """
    This function is that, it will filter the example stack based on the tag.
    it will filter based on example stack dataset from Miryung Kim.
    input parameter tag: tag you want to pick
    output_to_json: true or false
    """
    import os
    import subprocess
    import tqdm
    import json
    json_output_path = f"{json_output_path}{tag}.json"
    data_csv = read_csv('')
    dir_list = os.listdir('/et')
    json_output = {}
    for i in tqdm.tqdm(range(len(data_csv))):
        for j in range(2, len(data_csv[i]), 1):
            if tag in data_csv[i][j]:
                SO_ID = data_csv[i][0]
                GH_ID = data_csv[i][1]
                labels = data_csv[i][2:]
                for k in dir_list:
                    temp_list = os.listdir(f"/et/{k}")
                    for item in temp_list:
                        if item.startswith("so"):
                            curr_id = item
                            break
                    if curr_id == f"{SO_ID}.java":
                        if output_to_file == True:
                            os.makedirs(f"hri/{i}", exist_ok = True)
                            subprocess.run(["cp", "-r", f"/{k}/{curr_id}", f"/pede_adaptation/hri/{i}"])
                            subprocess.run(["cp", "-r", f"//{k}/{GH_ID}.txt", f"/pn/hri/{i}"])
                            subprocess.run(["cp", "-r", f"et/{k}/carved-{GH_ID}.java", f"/pe_adaptation/hri/{i}"])
                        if output_to_json == True:
                            SO_Code = open(f"/{k}/{curr_id}", 'rb').read().decode("utf-8",errors='ignore')
                            code_base = open(f"/{k}/{GH_ID}.txt", 'rb').read().decode("utf-8",errors='ignore')
                            craved_code = open(f"/{k}/carved-{GH_ID}.java", 'rb').read().decode("utf-8",errors='ignore')
                            json_output[curr_id.split(".")[0]] = {
                                "stackoverflow_snippet" : SO_Code,
                                "github_clone": GH_ID,
                                "manual_labels": labels,
                                "developer_edit": craved_code,
                                "github_codebase": code_base,
                            }
                            with open(json_output_path,'w') as outfile:
                                json.dump(json_output, outfile)
                    else:
                        pass
def search_for_SO_Post_with_SO_ID(ID):
    data_csv = read_csv('')
    dir_list = os.listdir('')
    SO_ID = None
    for i in range(len(data_csv)):
        if data_csv[i][0].split("-")[1] == ID:
            SO_ID = data_csv[i][0]
            GH_ID = data_csv[i][1]
            break
    if not SO_ID:
        print(f"ID {ID} not found")
        return None
    for k in dir_list:
        temp_list = os.listdir(f"/{k}")
        for item in temp_list:
            if item.startswith("so"):
                curr_id = item
                break
        if curr_id == f"{SO_ID}.java":
            SO_Code = open(f"//{k}/{curr_id}", 'rb').read().decode("utf-8",errors='ignore')
            code_base = open(f"/t/{k}/{GH_ID}.txt", 'rb').read().decode("utf-8",errors='ignore')
            craved_code = open(f"t/{k}/carved-{GH_ID}.java", 'rb').read().decode("utf-8",errors='ignore')
            removed_code_base = open(f"//{k}/{GH_ID}.txt", 'rb').read().decode("utf-8",errors='ignore')
            removed_code_base = split_lines_to_list(removed_code_base)
            start_line = GH_ID.split("-")[-2]
            end_line = GH_ID.split("-")[-1]
            removed_code_base = ''.join(removed_code_base[:int(start_line)-1] + removed_code_base[int(end_line):])
            return SO_Code, code_base, craved_code, removed_code_base
def llmgpt(system_prompt, user_prompt):
    return llm_gpt5(user_prompt=user_prompt, system_prompt=system_prompt)
if __name__ == "__main__":

    access_token = os.getenv("")
    patch = extract_GPT_answer(open("", 'r').read())
    code = open("", 'r').read()
    patch ="""\
--- MyJavaFile.java
+++ MyJavaFile.java
@@ -2,3 +2,3 @@
     public static void main(String[] args) {
-        String greeting = "Hello, World!";
+        String greeting = "Hi there!";
         System.out.println(greeting);
@@ -8,5 +8,5 @@
         if (isMorning) {
-            System.out.println("Good morning!");
+            System.out.println("Top of the morning to you!");
         } else {
-            System.out.println("Good afternoon!");
+            System.out.println("Good evening!");
         }
@@ -14,1 +14,2 @@
         System.out.println("Have a nice day!");
+        System.out.println("Enjoy your day!");
"""
    code = """\
public class Greeting {
    public static void main(String[] args) {
        String greeting = "Hello, World!";
        System.out.println(greeting);
        
        // Check if it's morning
        boolean isMorning = true;
        if (isMorning) {
            System.out.println("Good morning!");
        } else {
            System.out.println("Good afternoon!");
        }
        
        System.out.println("Have a nice day!");
    }
}

"""
    error = apply_patch_to_code(patch, code)
    print(error)
