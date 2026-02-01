public static long countLines(File file) {
	final int BUFFER_SIZE = 1024;
	if (file == null) {
		return 0L;
	}
	InputStream is = null;
	try {
		is = new BufferedInputStream(new FileInputStream(file));
		byte[] buffer = new byte[BUFFER_SIZE];
		long count = 0L;
		int readChars;
		boolean empty = true;
		while ((readChars = is.read(buffer)) != -1) {
			empty = false;
			for (int i = 0; i < readChars; ++i) {
				if (buffer[i] == '\n') {
					++count;
				}
			}
		}
		return (count == 0L && !empty) ? 1L : count;
	} catch (FileNotFoundException e) {
		e.printStackTrace();
		return 0L;
	} catch (IOException e) {
		e.printStackTrace();
		return 0L;
	} finally {
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}