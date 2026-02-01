/**
	 * Recursively zip the contents of a directory, preserving directory structure.
	 * 
	 * @param basePath relative path inside the zip (must end with '/' for directories)
	 * @param dir the directory whose contents should be added
	 * @param zipOut the ZipOutputStream to write entries to
	 * @throws IOException if an I/O error occurs while zipping
	 */
	private static void zipSubDirectory(final String basePath, final File dir, final ZipOutputStream zipOut) throws IOException {
		final int BUFFER_SIZE = 4096;
		final byte[] buffer = new byte[BUFFER_SIZE];

		if (dir == null || zipOut == null) {
			return;
		}

		final File[] files = dir.listFiles();
		if (files == null) {
			// Nothing to do (I/O error or not a directory)
			return;
		}

		for (File file : files) {
			if (file.isDirectory()) {
				final String path = basePath + file.getName() + "/";
				zipOut.putNextEntry(new ZipEntry(path));
				zipOut.closeEntry();
				zipSubDirectory(path, file, zipOut);
			} else {
				zipOut.putNextEntry(new ZipEntry(basePath + file.getName()));
				try (FileInputStream fileIn = new FileInputStream(file)) {
					int bytesRead;
					while ((bytesRead = fileIn.read(buffer)) > 0) {
						zipOut.write(buffer, 0, bytesRead);
					}
				}
				zipOut.closeEntry();
			}
		}
	}