String step5(String str) {

	// Guard: immediately return for single-character strings
	if (str == null || str.length() == 1)
		return str;

	int length = str.length();

	// --- Step 5a: terminal 'e' removal rules ---
	if (length > 1 && str.charAt(length - 1) == 'e') {
		String stem = str.substring(0, length - 1);
		int m = measure(stem);

		// remove 'e' if measure > 1, or if measure == 1 and stem is not cvc
		if (m > 1 || (m == 1 && !cvc(stem))) {
			str = stem;
			length = str.length(); // update length after modification
		}
	}

	// --- Step 5b: terminal "ll" reduction ---
	if (length > 1 && str.charAt(length - 1) == 'l' && str.charAt(length - 2) == 'l') {
		if (measure(str) > 1) {
			str = str.substring(0, length - 1);
		}
	}

	return str;
}