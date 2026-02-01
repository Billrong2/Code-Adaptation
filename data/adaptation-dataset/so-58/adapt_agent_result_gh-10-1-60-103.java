private static void addFile(File source, JarOutputStream target, CompilerOptions options) throws IOException {
	if (source == null || target == null || options == null || options.tempDirectory == null)
		return;

	// Compute path relative to the temp directory
	String basePath = options.tempDirectory.getCanonicalPath();
	String sourcePath = source.getCanonicalPath();
	String relativePath = "";
	if (sourcePath.startsWith(basePath)) {
		relativePath = sourcePath.substring(basePath.length());
		if (relativePath.startsWith(File.separator))
			relativePath = relativePath.substring(1);
	}
	// Normalize separators for jar entries
	relativePath = relativePath.replace(File.separatorChar, '/');

	if (source.isDirectory()) {
		// Do not add the top-level base directory itself
		if (!relativePath.isEmpty()) {
			if (!relativePath.endsWith("/"))
				relativePath += "/";
			JarEntry entry = new JarEntry(relativePath);
			entry.setTime(source.lastModified());
			target.putNextEntry(entry);
			target.closeEntry();
		}
		File[] children = source.listFiles();
		if (children != null) {
			for (File nestedFile : children) {
				addFile(nestedFile, target, options);
			}
		}
		return;
	}

	// Skip Java source files
	if (source.getName().endsWith(".java"))
		return;

	// Add file entry
	JarEntry entry = new JarEntry(relativePath);
	entry.setTime(source.lastModified());
	target.putNextEntry(entry);

	final int BUFFER_SIZE = 1024;
	try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(source))) {
		byte[] buffer = new byte[BUFFER_SIZE];
		int count;
		while ((count = in.read(buffer)) != -1) {
			target.write(buffer, 0, count);
		}
	}
	finally {
		target.closeEntry();
	}
}