int measure(final String word) {
	if (word == null || word.length() == 0) {
		return 0;
	}

	final int length = word.length();
	int m = 0;
	boolean inVowelRun = false;

	for (int i = 0; i < length; i++) {
		final char current = word.charAt(i);
		final char previous = (i == 0) ? 'a' : word.charAt(i - 1);
		final boolean isVowel = vowel(current, previous);

		if (isVowel) {
			inVowelRun = true;
		} else {
			if (inVowelRun) {
				m++;
				inVowelRun = false;
			}
		}
	}

	return m;
}