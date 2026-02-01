private static SortedMap<String, String> createParameterMap(final String query)
{
	// Return null for null or empty query strings
	if (query == null || query.length() == 0)
	{
		return null;
	}

	final SortedMap<String, String> queryParams = new TreeMap<String, String>();

	// Split on '&' and skip empty segments
	final String[] pairs = query.split("&");
	for (String pair : pairs)
	{
		if (pair == null || pair.length() == 0)
		{
			continue;
		}

		final int idx = pair.indexOf('=');
		final String key;
		final String value;

		if (idx < 0)
		{
			// No '=' present: key with empty value
			key = pair;
			value = "";
		}
		else if (idx == 0)
		{
			// Starts with '=': empty key with given value
			key = "";
			value = pair.substring(1);
		}
		else
		{
			// Split on the first '=' only
			key = pair.substring(0, idx);
			value = pair.substring(idx + 1);
		}

		// Use raw tokens; no URL decoding
		queryParams.put(key, value);
	}

	return queryParams;
}