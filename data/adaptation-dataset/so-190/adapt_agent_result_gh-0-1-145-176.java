private String sanitizeFileName(final String input) {
	// Null-safe: return empty filename if none provided
	if (input == null || input.isEmpty()) {
		return "";
	}

	final int length = input.length();
	char[] buffer = this.oldChars;
	if (buffer == null || buffer.length < length) {
		// grow reusable buffer only if needed
		buffer = new char[length];
		this.oldChars = buffer;
	}

	int out = 0;
	boolean modified = false;
	for (int i = 0; i < length; i++) {
		final char ch = input.charAt(i);
		// allow printable characters except semicolon
		if (ch >= ' ' && ch != ';') {
			buffer[out++] = ch;
		} else {
			modified = true;
		}
	}

	// If nothing was stripped, avoid allocation and return original
	if (!modified) {
		return input;
	}

	return new String(buffer, 0, out);
}