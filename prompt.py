user_prompt_gpt_add_feature_naive = """
Given the following GitHub code, I want you to add a function between <\purpose1> and <\purpose2>. 
<\code>
Write a single function that takes in a file path, decodes the file content, and integrates seamlessly into the existing code. 
Show me the implementation and explain how it fits into the codebase.
"""
user_prompt_gpt_add_feature_with_summarization = """
Write a single function that will suffice the following requirements:
You need to adaptate the code snippet from Stack Overflow to the existing codebase. If there are some function that related to Stack Overflow code, try adapt the code in similar coding style. Here are some summary of the Stack Overflow post:
<\instructions>

Given the following GitHub code, I want you to implement a function between <\purpose1> and <\purpose2>, that following instructions mentioned above, please carefully read the code and the comment and try to understand what it is missing.
<\code>
Additionally, here is a plan for adapting the snippet:
[ADAPTATION_PLAN]

Please return:
1) A unified diff (patch) that only adding necessary part of Stack Overflow code in Github Code according to instructions and the adaptation plan that replicate the same behavior as the Stack Overflow code. If there are some function that related to Stack Overflow code, try adapt the code in similar coding style and data types.
2) A short explanation of how the patch integrates into the codebase.

Disclaimer:
In your patch, do not delete any code that is currently existing in the code base.
"""

user_prompt_gpt_naive = """
You are a coding assistant.
Here is a snippet from our GitHub repository: 
<REPO_CODE_SNIPPET>
And here is the code snippet from Stack Overflow: 
<SO_CODE_SNIPPET>
Please add the Stack Overflow feature into our existing snippet.
I only need the final patch—do not provide the entire GitHub file, just show the unified diff or patch.
"""

## this is a specific example, only tuned for test_labeled_2

SO_post_summarization_system_prompt = """
You are an assistant specialized in summarizing Stack Overflow Q&A posts. Your goal is to produce a concise, structured summary of the provided question and accepted answer, focusing on essential technical details. Follow the given instructions and output format strictly. If there are multiple methods or classes, write summary for each of them and clearly labeled them in readable manner.
"""
SO_post_summarization_user_prompt = """
Given the full text of a Stack Overflow question and its accepted answer, produce a concise summary following the template below. Focus only on essential technical details and direct relevance to the solution. If a category does not apply, omit it from the final output.
Stack Overflow Post Summary:
1. Question Title: [Descriptive title of the question]
2. Core Problem: [Sentences describing the main issue the OP wants to solve]
3. Relevant Environment/Constraints: [List any important details such as programming language, platform, version constraints, libraries, frameworks, or performance limitations mentioned in the post]
4. Accepted Answer's Core Idea: [For each method/class, summarizing how the accepted answer solves the problem and how the answer do the implementation]
5. Detailed Snippet Explanation. If there are multiple methods or classes, write summary for each of them and clearly label each of them and their summarization in readable manner:
-[Bullet points explaining every key part of the accepted answer’s code and how they contribute to the solution]
Potential Edge Cases Mentioned:
-[Bullet points for any special conditions, known issues, or caveats highlighted by the answer or comments]
<\SO_POST>
"""
#SO_POST is the holder for PASTE THE FULL STACK OVERFLOW QUESTION AND ACCEPTED ANSWER HERE
guided_self_planning_phase1_system_prompt = """
You are ChatGPT, an assistant specialized in adapting code snippets to new contexts. 
You have these resources:
1) A code snippet or method signature from Stack Overflow.
2) A partial call graph (or relevant codebase context).
3) A list of adaptation categories from the paper "Analyzing and Supporting Adaptation of Online Code Examples."
<\add 6 adaptation catagory>
Your goal in this phase is ONLY to analyze potential adaptation issues and produce a plan. 
Do not write the final code here.
"""
guided_self_planning_phase1_user_prompt = """
Below is all the information you need.
<\code>
<repo_level_context>  // e.g., caller-callee details, relevant classes, etc.
<\SO_POST>            // Ignore for now (SO post will be parsed later by SO ID)
<adaptation_catagories>       // E.g., Code Hardening, Resolve Compilation Errors, Exception Handling, Logic Customization, Refactoring, Miscellaneous
1) Code Hardening: Enhancing robustness by adding condition-
als, handling new exception types, inserting final modifiers, and
managing resources (e.g., closing streams).
2) Resolving Compilation Errors: Declaring variables, correcting
method calls, removing references to undefined methods, etc.
3) Exception Handling: Modifying try-catch blocks, updating ex-
ception types, and refining catch/finally logic.
4) Logic Customization: Adjusting method calls, changing con-
stants or variable types, and refining conditionals for adaptation.
5) Refactoring: Improving maintainability by renaming, replacing
magic values, and restructuring inline fields.
6) Other adjustments: Minor modifications, e.g., altering log state-
ments, reformatting code, or updating annotations and comments.
Instructions:
1) Review the snippet in light of the categories. 
2) Produce a structured plan organized by the six categories (use the exact labels below).
   - Include all six categories; if a category doesn't apply, state "none".
   - Propose how you will address any needed changes (e.g., handle new exception type, rename variables).
3) Do not generate final code. 
4) Do not add extra commentary outside the plan.
5) Only provide the plan, no explanation is needed.
"""
#EXPECTED OUTPUT (Phase 1):
#A "Self-Planning" section in which you list each relevant category from [ADAPTATION_CATEGORIES], describe necessary changes, and explain how you will implement them in the final code.
guided_self_planning_phase2_system_prompt = """
You are Java Programmer, an assistant specialized in adapting code snippets into existing repositories. You have been provided with:

1) A snippet or method signature from Stack Overflow
2) A call graph or other relevant code context
3) A summary of the Stack Overflow post
4) A set of adaptation categories.
5) A previously generated plan detailing what changes are needed

Your task is to produce a unified diff or patch that applies all the planned changes to the target repository file(s). 
After generating the patch, provide a brief explanation of how each change addresses the adaptation categories. 
Do not include the entire updated file—only the necessary diff that shows what to add, modify, or remove.

"""
guided_self_planning_phase2_user_prompt = """
Below is the plan detailing required adaptations, along with the original code context and summarization.

[ADAPTATION_PLAN]
[SNIPPET_OR_METHOD_SIGNATURE]
[CALL_GRAPH_OR_CODE_CONTEXT]
[SO_SUMMARIZATION]

Please generate:
1) A unified patch that implements target code into the existing file-level code.
2) Incorporates all the adaptations described in [ADAPTATION_PLAN].
3) Provide a short explanation of the changes made in the patch.

Remember:
- Follow the repository style indicated by the call graph or code context.
- Provide well-formatted code that fits seamlessly into the existing codebase.
"""



universal_ablation_prompt = """
Write a single function that will suffice the following requirements:
You need to adaptate the code snippet from Stack Overflow to the existing codebase. If there are some function that related to Stack Overflow code, try adapt the code in similar coding style. Here are some summary of the Stack Overflow post:
<\instructions>

Given the following GitHub code, I want you to implement a function between <\purpose1> and <\purpose2>, that following instructions mentioned above, please carefully read the code and the comment and try to understand what it is missing.
<\code>
Additionally, here is a plan for adapting the snippet:
[ADAPTATION_PLAN]

Please return:
1) Target adapted Stack Overflow code in Github Code according to instructions and the adaptation plan that replicate the same behavior as the Stack Overflow code. If there are some function that related to Stack Overflow code, try adapt the code in similar coding style and data types.
2) Only return the new adapted target code, you do not return the entire codebase.
2) A short explanation of how the function integrates into the codebase.

Disclaimer:
In your generated function, do not delete any code that is currently existing in the code base.
"""



adaptation_system= """
System Prompt:
You are a senior code-adaptation engineer. Given (a) the developer’s INTENT, (b) an adaptation PLAN organized by the six categories, (c) the original Stack Overflow post (question + answer snippet), and (d) the target GitHub codebase BEFORE adaptation, generate the SINGLE adapted function (signature + body) that bridges the gap from the SO snippet to the project’s desired behavior. This is a functional-level generation task.

Hard rules:

1. Return ONLY a valid JSON object with exactly two string keys: "code" and "explanation". No markdown, no extra keys, no metadata.

2. "code" must contain exactly one complete function (signature + body). Do not output any other parts of the file/repo, no package/import statements, no extra helpers/classes/files, no diffs. Do not repeat the entire codebase.

3. There is no explicit repo conventions section, target file path, or target function signature. You must infer a conforming function from the BEFORE codebase, INTENT, and PLAN:

   a). If an existing function clearly corresponds to the target, preserve its name, access modifier, return type, and parameter order/types; adapt its body accordingly.

   b). If no clear target exists, infer a minimal, safe function consistent with the project and PLAN. Prefer existing project types; when imports would be needed, use fully qualified names inside the function.

4. Apply the six categories entirely within this single function:

   a). code_hardening (null/empty checks, validation, resource cleanup, final where appropriate)

   b). resolve_compilation_errors (declare/adjust locals; fix types/FQNs; prefer fully qualified names where imports aren’t shown)

   c). exception_handling (introduce try/catch/throws per PLAN; align with patterns observable in nearby code if provided)

   d). logic_customization (adjust calls/params/conditions/constants/algorithms; integrate any new custom API calls from the PLAN that were not in the SO snippet)

   e). refactoring (rename locals, replace magic values, restructure within the function only)

   f). misc (brief comments/logging consistent with observed style; minimal formatting)

5. "explanation" must be a natural-language write-up organized exactly into the six sections above (use these exact lowercase headings), and for each section explicitly state what you did in this function. When you use any new custom API calls that were not in the SO snippet, mention them under the most appropriate section (typically logic_customization or resolve_compilation_errors) with fully qualified names/signatures if observable; if none, write “none”.

6. If any detail is not derivable, choose the safest minimal default consistent with INTENT and PLAN, or write “unknown” where unavoidable.
"""

adaptation_user = """
Materials:

INTENT (string): 
<INTENT>

PLAN (string; organized by the six categories and listing any new custom API calls to use): 
<PLAN>

SO_POST (ignored for now; will be parsed later by SO ID): 
<FULL POST>

SO_ANSWER_SNIPPET (code to adapt from): 
<ANSWER>

GITHUB_CODEBASE_BEFORE (only the relevant file content and small surrounding context; do not paste the entire repo):
<CODE BASE>

SIBLING_METHODS_SUMMARY (optional; may be empty):
<SIBLING_METHODS_SUMMARY>

VERIFICATION_ERRORS (optional; may be empty):
<VERIFICATION_ERRORS>


Task:
Using INTENT and PLAN as authoritative guidance, adapt SO_ANSWER_SNIPPET into the target project context provided by GITHUB_CODEBASE_BEFORE (and SIBLING_METHODS_SUMMARY if present) and produce exactly one function that implements the desired behavior. Do not repeat the entire codebase. Return ONLY the JSON object below, with your single adapted function in "code" and a six-section explanation in "explanation":

{
"code": "<ONE complete function (signature + body) only>",
"explanation": "code_hardening: ...\nresolve_compilation_errors: ...\nexception_handling: ...\nlogic_customization: ...\nrefactoring: ...\nmisc: ...\n(For any new custom API calls used that were absent from the SO snippet, name and justify them in the relevant section with FQNs/signatures if visible; write 'none' if not applicable.)"
}

"""
