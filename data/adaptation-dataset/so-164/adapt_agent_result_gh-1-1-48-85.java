private byte[] readTileImage(int x, int y, int zoom) {
	java.io.InputStream in = null;
	ByteArrayOutputStream buffer = null;

	String tileFilename = getTileFilename(x, y, zoom);
	String mapsDir = DirectoryPath.getMapsPath();
	if (tileFilename == null || tileFilename.length() == 0 || mapsDir == null || mapsDir.length() == 0) {
		return null;
	}

	String fullPath = mapsDir + java.io.File.separator + tileFilename;
	java.io.File tileFile = new java.io.File(fullPath);
	if (!tileFile.exists() || !tileFile.isFile()) {
		return null;
	}

	try {
		in = new FileInputStream(tileFile);
		buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] data = new byte[BUFFER_SIZE];

		while ((nRead = in.read(data, 0, BUFFER_SIZE)) != -1) {
			buffer.write(data, 0, nRead);
		}
		buffer.flush();

		return buffer.toByteArray();
	} catch (FileNotFoundException e) {
		return null;
	} catch (IOException e) {
		return null;
	} catch (OutOfMemoryError e) {
		e.printStackTrace();
		return null;
	} finally {
		if (in != null) try { in.close(); } catch (Exception ignored) {}
		if (buffer != null) try { buffer.close(); } catch (Exception ignored) {}
	}
}