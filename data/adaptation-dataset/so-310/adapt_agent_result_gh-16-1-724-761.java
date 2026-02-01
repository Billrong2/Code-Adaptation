public static void unzipToDirectory(File zipFile, String destinationDirPath) throws java.util.zip.ZipException, java.io.IOException {
	if (zipFile == null || !zipFile.isFile()) {
		throw new java.io.IOException("zipFile must be an existing file");
	}
	if (destinationDirPath == null) {
		throw new java.io.IOException("destinationDirPath must not be null");
	}
	final File destinationDir = new File(destinationDirPath);
	if (!destinationDir.exists() && !destinationDir.mkdirs()) {
		throw new java.io.IOException("Could not create destination directory: " + destinationDir.getAbsolutePath());
	}
	final int BUFFER_SIZE = 2048;
	try (java.util.zip.ZipFile zip = new java.util.zip.ZipFile(zipFile)) {
		java.util.Enumeration<? extends java.util.zip.ZipEntry> entries = zip.entries();
		while (entries.hasMoreElements()) {
			java.util.zip.ZipEntry entry = entries.nextElement();
			File destFile = new File(destinationDir, entry.getName());
			if (entry.isDirectory()) {
				if (!destFile.exists() && !destFile.mkdirs()) {
					throw new java.io.IOException("Could not create directory: " + destFile.getAbsolutePath());
				}
				continue;
			}
			File parent = destFile.getParentFile();
			if (parent != null && !parent.exists() && !parent.mkdirs()) {
				throw new java.io.IOException("Could not create directory: " + parent.getAbsolutePath());
			}
			try (java.io.InputStream in = new java.io.BufferedInputStream(zip.getInputStream(entry));
				 java.io.OutputStream out = new java.io.BufferedOutputStream(new java.io.FileOutputStream(destFile), BUFFER_SIZE)) {
				byte[] buffer = new byte[BUFFER_SIZE];
				int read;
				while ((read = in.read(buffer)) != -1) {
					out.write(buffer, 0, read);
				}
			}
		}
	}
}