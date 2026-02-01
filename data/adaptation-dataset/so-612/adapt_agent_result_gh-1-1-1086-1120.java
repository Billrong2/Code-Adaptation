public static String readLastLine(String filePath) {
	RandomAccessFile fileHandler = null;
	try {
		File file = new File(filePath);
		fileHandler = new RandomAccessFile(file, "r");
		long fileLength = file.length() - 1;
		StringBuilder sb = new StringBuilder();

		for (long filePointer = fileLength; filePointer != -1; filePointer--) {
			fileHandler.seek(filePointer);
			int readByte = fileHandler.readByte();

			if (readByte == 0xA) {
				if (filePointer == fileLength) {
					continue;
				} else {
					break;
				}
			} else if (readByte == 0xD) {
				if (filePointer == fileLength - 1) {
					continue;
				} else {
					break;
				}
			} else {
				sb.append((char) readByte);
			}
		}

		String lastLine = sb.reverse().toString();
		return lastLine;
	} catch (FileNotFoundException e) {
		e.printStackTrace();
		return null;
	} catch (IOException e) {
		e.printStackTrace();
		return null;
	}
}