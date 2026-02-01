public static int countLines(File file) throws Exception {
	// Counts lines by counting newline characters (\n) only.
	// Returns 1 only if the file is non-empty and contains no newline characters.
	if (file == null) {
		throw new IllegalArgumentException("file must not be null");
	}

	BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
	byte[] buffer = new byte[1024];
	int bytesRead;
	int newlineCount = 0;
	boolean hasAnyByte = false;

	while ((bytesRead = inputStream.read(buffer)) != -1) {
		hasAnyByte = true;
		for (int i = 0; i < bytesRead; i++) {
			if (buffer[i] == '\n') {
				newlineCount++;
			}
		}
	}

	// Explicitly close the stream after reading
	inputStream.close();

	// If the file is non-empty and contains no newlines, it has exactly one line
	if (hasAnyByte && newlineCount == 0) {
		return 1;
	}

	return newlineCount;
}