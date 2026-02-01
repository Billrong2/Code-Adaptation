public void add(File baseDirectory, String relativePath, boolean removeFlag) throws IOException {
	if (baseDirectory == null || !baseDirectory.exists() || !baseDirectory.isDirectory()) {
		throw new IllegalArgumentException("baseDirectory must exist and be a directory");
	}
	if (relativePath == null) {
		throw new IllegalArgumentException("relativePath must not be null");
	}
	if (this.target == null) {
		throw new IllegalStateException("JarOutputStream has not been started");
	}

	File currentFile = relativePath.length() == 0 ? baseDirectory : new File(baseDirectory, relativePath);
	if (!currentFile.exists()) {
		return; // nothing to do
	}

	String entryName = relativePath.replace('\\', '/');
	BufferedInputStream in = null;
	try {
		if (currentFile.isDirectory()) {
			if (!entryName.isEmpty() && !entryName.endsWith("/")) {
				entryName = entryName + "/";
			}
			if (!entryName.isEmpty()) {
				JarEntry entry = new JarEntry(entryName);
				entry.setTime(currentFile.lastModified());
				target.putNextEntry(entry);
				target.closeEntry();
			}

			String[] children = currentFile.list();
			if (children != null) {
				for (String child : children) {
					String childRelativePath = entryName.isEmpty() ? child : entryName + child;
					add(baseDirectory, childRelativePath, removeFlag);
				}
			}
		} else {
			JarEntry entry = new JarEntry(entryName);
			entry.setTime(currentFile.lastModified());
			target.putNextEntry(entry);
			in = new BufferedInputStream(new FileInputStream(currentFile));
			byte[] buffer = new byte[1024];
			while (true) {
				int count = in.read(buffer);
				if (count == -1) break;
				target.write(buffer, 0, count);
			}
			target.closeEntry();
		}
	} finally {
		if (in != null) {
			try { in.close(); } catch (IOException e) { /* ignore */ }
		}
		if (removeFlag) {
			try { currentFile.delete(); } catch (SecurityException e) { /* ignore */ }
		}
	}
}