
	/**
	 * Replaces all occurrences of {@code target} in {@code source} with {@code replacement},
	 * performing the match in a case-insensitive manner while preserving the original
	 * character casing outside the replaced regions.
	 *
	 * @param source the source string to operate on
	 * @param target the string to find (case-insensitive)
	 * @param replacement the replacement string
	 * @return a new string with all case-insensitive occurrences of {@code target} replaced,
	 *         or {@code source} if no replacement can be performed
	 */
	public static String replaceIgnoreCase(final String source, final String target, final String replacement) {
		// Hardening: null and empty checks
		if (source == null || target == null || replacement == null) {
			return source;
		}
		if (target.length() == 0) {
			// Avoid infinite loop; nothing sensible to replace
			return source;
		}

		StringBuilder result = new StringBuilder(source);
		StringBuilder sourceLower = new StringBuilder(source.toLowerCase());
		String searchString = target.toLowerCase();

		int idx = 0;
		while ((idx = sourceLower.indexOf(searchString, idx)) != -1) {
			result.replace(idx, idx + searchString.length(), replacement);
			sourceLower.replace(idx, idx + searchString.length(), replacement);
			idx += replacement.length();
		}

		return result.toString();
	}
