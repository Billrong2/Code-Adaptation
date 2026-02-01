private static SortedMap<String, String> CreateParameterMap(final String queryString) {
	if (queryString == null || queryString.length() == 0)
		return null;

	final SortedMap<String, String> params = new TreeMap<String, String>();
	final String[] pairs = queryString.split("&");
	for (final String pair : pairs) {
		if (pair == null || pair.length() == 0)
			continue;

		final String[] kv = pair.split("=", 2);
		String key;
		String value;

		if (kv.length == 1) {
			// No '=' present
			key = kv[0];
			value = "";
		}
		else {
			key = kv[0];
			value = kv[1];
		}

		try {
			key = URLDecoder.decode(key, "UTF-8");
		}
		catch (IllegalArgumentException | UnsupportedEncodingException e) {
			System.err.println("Failed to URL-decode query parameter key: " + key);
		}
		try {
			value = URLDecoder.decode(value, "UTF-8");
		}
		catch (IllegalArgumentException | UnsupportedEncodingException e) {
			System.err.println("Failed to URL-decode query parameter value: " + value);
		}

		// Later occurrences overwrite earlier ones
		params.put(key, value);
	}

	return params.isEmpty() ? null : params;
}