private static SortedMap<String, String> createParameterMap(final String query) {
		// Return null for null or empty query string
		if (query == null || query.length() == 0) {
			return null;
		}

		// Use a TreeMap to ensure lexicographical ordering of parameters
		final SortedMap<String, String> paramMap = new TreeMap<String, String>();

		// Split on '&' into key/value pairs, skipping empty pairs
		final String[] pairs = query.split("&");
		for (String pair : pairs) {
			if (pair == null || pair.length() == 0) {
				continue;
			}

			// Split on '=' with a max of 2 parts to preserve empty keys/values
			final String[] parts = pair.split("=", 2);
			final String key;
			final String value;

			if (parts.length == 2) {
				// Handles normal "key=value" and leading "=value" (empty key)
				key = parts[0];
				value = parts[1];
			} else {
				// Handles key-only parameters with no '='
				key = parts[0];
				value = "";
			}

			// Overwrite duplicates by design; no decoding or filtering
			paramMap.put(key, value);
		}

		return paramMap;
	}