private static String Canonicalize(final SortedMap<String, String> params) {
	if (params == null || params.isEmpty())
		return "";
	final StringBuilder encodedQuery = new StringBuilder();
	for (final Map.Entry<String, String> entry : params.entrySet()) {
		final String rawKey = entry.getKey();
		if (rawKey == null)
			continue;
		final String rawValue = entry.getValue();
		final String encodedKey = PercentEncodeRfc3986(rawKey);
		final String encodedValue = rawValue != null ? PercentEncodeRfc3986(rawValue) : "";
		if (encodedQuery.length() > 0)
			encodedQuery.append('&');
		encodedQuery.append(encodedKey).append('=').append(encodedValue);
	}
	return encodedQuery.toString();
}