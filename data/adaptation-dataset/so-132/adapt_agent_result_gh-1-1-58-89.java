public static boolean copyJarResourcesRecursively(final File destDir, final JarURLConnection jarConn, final FilenameFilter filter) throws IOException {
	// validate inputs
	if (destDir == null || jarConn == null || filter == null) {
		throw new IllegalArgumentException("destDir, jarConn and filter must not be null");
	}
	if (!destDir.exists() && !ensureDirectoryExists(destDir)) {
		throw new IOException("Cannot create destination directory: " + destDir);
	}
	if (!destDir.isDirectory() || !destDir.canWrite()) {
		throw new IOException("Destination is not a writable directory: " + destDir);
	}

	final JarFile jarFile = jarConn.getJarFile();
	final String entryPrefix = jarConn.getEntryName();
	final String normalizedPrefix = entryPrefix == null ? "" : (entryPrefix.endsWith("/") ? entryPrefix : entryPrefix + "/");
	final String destRootPath = destDir.getCanonicalPath() + File.separator;

	for (final Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements();) {
		final JarEntry entry = e.nextElement();
		final String entryName = entry.getName();

		// select only entries under the requested prefix
		if (!Strings.isNullOrEmpty(normalizedPrefix) && !entryName.startsWith(normalizedPrefix)) {
			continue;
		}
		// skip directories
		if (entry.isDirectory()) {
			continue;
		}

		// compute destination-relative path
		String relativePath = removeStart(entryName, normalizedPrefix);
		if (relativePath.startsWith("/")) {
			relativePath = relativePath.substring(1);
		}

		final File destFile = new File(destDir, relativePath);

		// guard against path traversal
		final String destFilePath = destFile.getCanonicalPath();
		if (!destFilePath.startsWith(destRootPath)) {
			throw new IOException("Illegal jar entry path: " + entryName);
		}

		// apply filename filter
		final File parentDir = destFile.getParentFile();
		if (!filter.accept(parentDir, destFile.getName())) {
			continue;
		}

		// ensure parent directories exist
		if (parentDir != null && !ensureDirectoryExists(parentDir)) {
			throw new IOException("Cannot create directory: " + parentDir);
		}

		// copy entry contents
		try (InputStream is = jarFile.getInputStream(entry)) {
			if (!copyStream(is, destFile)) {
				return false; // per-file copy failure
			}
		}
	}
	return true;
}
