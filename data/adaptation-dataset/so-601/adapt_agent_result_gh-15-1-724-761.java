static public void unzipToDirectory(File zipFile, String outputDirectoryPath) throws ZipException, IOException {
	final int BUFFER_SIZE = 8192;
	if (zipFile == null || !zipFile.exists() || !zipFile.isFile()) {
		throw new IOException("Zip file does not exist or is not a file: " + zipFile);
	}
	if (outputDirectoryPath == null || outputDirectoryPath.trim().isEmpty()) {
		throw new IOException("Output directory path must be specified");
	}
	File outputDirectory = new File(outputDirectoryPath);
	if (!outputDirectory.exists()) {
		if (!outputDirectory.mkdirs()) {
			throw new IOException("Could not create output directory: " + outputDirectory.getAbsolutePath());
		}
	}
	if (!outputDirectory.isDirectory()) {
		throw new IOException("Output path is not a directory: " + outputDirectory.getAbsolutePath());
	}

	try (ZipFile zip = new ZipFile(zipFile)) {
		java.util.Enumeration<? extends ZipEntry> entries = zip.entries();
		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			File destFile = new File(outputDirectory, entry.getName());
			File parentDir = destFile.getParentFile();
			if (parentDir != null && !parentDir.exists()) {
				parentDir.mkdirs();
			}

			if (!entry.isDirectory()) {
				try (BufferedInputStream in = new BufferedInputStream(zip.getInputStream(entry));
					 BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(destFile), BUFFER_SIZE)) {
					byte[] buffer = new byte[BUFFER_SIZE];
					int count;
					while ((count = in.read(buffer)) != -1) {
						out.write(buffer, 0, count);
					}
				}
			}
		}
	}
}