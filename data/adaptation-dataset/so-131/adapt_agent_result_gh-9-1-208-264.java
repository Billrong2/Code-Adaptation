public static boolean extractFile(File input, File output) {
	// Validate inputs
	if (input == null || output == null || !input.exists() || !input.isFile()) {
		return false;
	}

	// Derive extraction root from the parent of the output file
	File rootDir = output.getParentFile();
	if (rootDir == null) {
		return false;
	}
	if (!rootDir.exists() && !rootDir.mkdirs()) {
		return false;
	}

	final int BUFFER_SIZE = 1024;
	byte[] buffer = new byte[BUFFER_SIZE];

	try (java.util.zip.ZipInputStream zis = new java.util.zip.ZipInputStream(
			new java.io.BufferedInputStream(new java.io.FileInputStream(input)))) {
		java.util.zip.ZipEntry entry;

		while ((entry = zis.getNextEntry()) != null) {
			String entryName = entry.getName();

			// Skip junk entries
			if (isJunkFilename(entryName)) {
				zis.closeEntry();
				continue;
			}

			File outFile = new File(rootDir, entryName);

			// Prevent Zip Slip by validating canonical paths
			String rootPath = rootDir.getCanonicalPath();
			String outPath = outFile.getCanonicalPath();
			if (!outPath.startsWith(rootPath + File.separator)) {
				zis.closeEntry();
				return false;
			}

			if (entry.isDirectory()) {
				if (!outFile.exists() && !outFile.mkdirs()) {
					zis.closeEntry();
					return false;
				}
				zis.closeEntry();
				continue;
			}

			// Ensure parent directories exist
			File parent = outFile.getParentFile();
			if (parent != null && !parent.exists() && !parent.mkdirs()) {
				zis.closeEntry();
				return false;
			}

			try (java.io.FileOutputStream fout = new java.io.FileOutputStream(outFile)) {
				int count;
				while ((count = zis.read(buffer)) != -1) {
					fout.write(buffer, 0, count);
				}
			}

			zis.closeEntry();
		}
	} catch (java.io.IOException e) {
		e.printStackTrace();
		return false;
	}

	return true;
}