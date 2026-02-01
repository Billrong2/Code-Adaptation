String step2(String str) {
	// Porter Stemmer Step 2: suffix replacement
	if (str == null || str.length() == 0)
		return str;

	// Ordered mapping of suffix -> replacement (longer suffixes first)
	final java.util.Map<String, String> step2SuffixMap = new java.util.LinkedHashMap<String, String>();
	step2SuffixMap.put("ational", "ate");
	step2SuffixMap.put("tional", "tion");
	step2SuffixMap.put("ization", "ize");
	step2SuffixMap.put("isation", "ize");
	step2SuffixMap.put("enci", "ence");
	step2SuffixMap.put("anci", "ance");
	step2SuffixMap.put("izer", "ize");
	step2SuffixMap.put("iser", "ize");
	step2SuffixMap.put("abli", "able");
	step2SuffixMap.put("alli", "al");
	step2SuffixMap.put("entli", "ent");
	step2SuffixMap.put("eli", "e");
	step2SuffixMap.put("ousli", "ous");
	step2SuffixMap.put("ation", "ate");
	step2SuffixMap.put("ator", "ate");
	step2SuffixMap.put("alism", "al");
	step2SuffixMap.put("iveness", "ive");
	step2SuffixMap.put("fulness", "ful");
	step2SuffixMap.put("ousness", "ous");
	step2SuffixMap.put("aliti", "al");
	step2SuffixMap.put("iviti", "ive");
	step2SuffixMap.put("biliti", "ble");
	step2SuffixMap.put("logi", "log");

	NewString stem = new NewString();

	for (java.util.Map.Entry<String, String> entry : step2SuffixMap.entrySet()) {
		String suffix = entry.getKey();
		String replacement = entry.getValue();
		if (hasSuffix(str, suffix, stem)) {
			if (measure(stem.str) > 0) {
				str = stem.str + replacement;
				return str;
			}
		}
	}
	return str;
  }