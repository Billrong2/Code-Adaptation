public static List<String> splitConsideringQuotes(String input, char delimiter) {
	// Quote-aware split using a custom delimiter; double-quotes are stripped from results
	List<String> result = new ArrayList<String>();
	
	if (input == null || input.isEmpty()) {
		return result;
	}
	
	// Delimiter validation: do not allow quote as delimiter
	if (delimiter == '"') {
		return result;
	}
	
	int start = 0;
	boolean inQuotes = false;
	
	for (int current = 0; current < input.length(); current++) {
		char ch = input.charAt(current);
		
		if (ch == '"') {
			inQuotes = !inQuotes; // toggle quote state
		}
		
		if (ch == delimiter && !inQuotes) {
			String token = input.substring(start, current);
			// strip all quote characters from token
			token = StringUtils.replace(token, "\"", "");
			result.add(token);
			start = current + 1;
		}
	}
	
	// Always add the final token after iteration
	if (start <= input.length()) {
		String token = input.substring(start);
		token = StringUtils.replace(token, "\"", "");
		result.add(token);
	}
	
	return result;
}