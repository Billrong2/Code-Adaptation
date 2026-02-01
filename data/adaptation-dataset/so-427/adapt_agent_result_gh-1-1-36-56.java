public static boolean copyFilesRecusively(final File toCopy, final File destDir, final FilenameFilter filter) {
	if (toCopy == null || destDir == null || filter == null) {
		return false;
	}
	if (!destDir.exists() && !destDir.mkdirs()) {
		return false;
	}
	if (!destDir.isDirectory()) {
		return false;
	}

	if (!toCopy.isDirectory()) {
		final File parentDir = toCopy.getParentFile();
		final String fileName = toCopy.getName();
		// apply filter only to files; rejection is treated as success
		if (!filter.accept(parentDir, fileName)) {
			return true;
		}
		return copyFile(toCopy, new File(destDir, fileName));
	}

	final File newDestDir = new File(destDir, toCopy.getName());
	if (!newDestDir.exists() && !newDestDir.mkdir()) {
		return false;
	}
	final File[] children = toCopy.listFiles();
	if (children == null) {
		// unable to list directory contents; fail safely
		return false;
	}
	for (final File child : children) {
		if (!copyFilesRecusively(child, newDestDir, filter)) {
			return false;
		}
	}
	return true;
}