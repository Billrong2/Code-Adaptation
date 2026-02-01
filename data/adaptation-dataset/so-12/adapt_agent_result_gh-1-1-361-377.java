private String stripPrefixes(String str) {
	// Defensive checks
	if (str == null || str.length() == 0) {
		return str;
	}

	// Fixed list of supported prefixes (case-sensitive; caller lowercases input)
	final String[] prefixes = {
		"kilo", "micro", "milli", "intra", "ultra", "mega", "nano", "pico", "pseudo"
	};

	for (int i = 0; i < prefixes.length; i++) {
		String prefix = prefixes[i];
		if (str.length() > prefix.length() && str.startsWith(prefix)) {
			return str.substring(prefix.length());
		}
	}

	return str;
}