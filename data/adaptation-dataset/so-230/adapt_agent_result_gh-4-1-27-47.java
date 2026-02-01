public static Map<String, List<String>> parseQueryString(final String queryString) throws UnsupportedEncodingException {
	final Map<String, List<String>> params = new HashMap<>();
	if (queryString == null || queryString.isEmpty()) {
		return params;
	}

	// Decode the entire query first so encoded separators (e.g. %26) are handled correctly
	final String decodedQuery = URLDecoder.decode(queryString, "UTF-8");
	final String[] pairs = decodedQuery.split("&");

	for (final String pair : pairs) {
		if (pair == null || pair.isEmpty()) {
			continue;
		}
		final String[] parts = pair.split("=", 2);
		final String key = parts.length > 0 && !parts[0].isEmpty()
				? URLDecoder.decode(parts[0], "UTF-8")
				: "";
		final String value = parts.length > 1
				? URLDecoder.decode(parts[1], "UTF-8")
				: "";

		List<String> values = params.get(key);
		if (values == null) {
			values = new ArrayList<>();
			params.put(key, values);
		}
		values.add(value);
	}
	return params;
}