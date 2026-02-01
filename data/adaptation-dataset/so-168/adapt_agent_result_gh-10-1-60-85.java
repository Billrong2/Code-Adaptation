/**
	 * Counts the number of lines in the given file by scanning raw bytes and
	 * counting newline characters. This avoids character decoding overhead and
	 * is generally faster than line-by-line readers for large files.
	 *
	 * @param file the file whose lines should be counted
	 * @return the number of lines in the file
	 * @throws Exception if any I/O or other error occurs
	 */
	public static int countLines(File file) throws Exception {
		BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file));

		byte[] buffer = new byte[1024];
		int count = 0;
		int readChars = 0;
		boolean empty = true;

		while ((readChars = stream.read(buffer)) != -1) {
			empty = false;
			for (int i = 0; i < readChars; ++i) {
				if (buffer[i] == '\n') {
					++count;
				}
			}
		}

		stream.close();

		return (count == 0 && !empty) ? 1 : count;
	}