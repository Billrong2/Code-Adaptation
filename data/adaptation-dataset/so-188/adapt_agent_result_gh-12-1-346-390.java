/**
	 * Recursively copies a file or directory to the given target location.
	 * <p>
	 * If {@code sourceLocation} is a directory, all children will be copied
	 * recursively. The {@code targetLocation} (and any required parent
	 * directories) will be created if they do not already exist.
	 * </p>
	 *
	 * @param sourceLocation the source file or directory to copy (must not be null)
	 * @param targetLocation the destination file or directory (must not be null)
	 * @throws IOException if an I/O error occurs during copying or directory creation
	 */
	public static void copy(final File sourceLocation, final File targetLocation) throws IOException {
		if (sourceLocation == null) {
			throw new IOException("Source location is null");
		}
		if (targetLocation == null) {
			throw new IOException("Target location is null");
		}

		if (sourceLocation.isDirectory()) {
			if (!targetLocation.exists() && !targetLocation.mkdirs()) {
				throw new IOException("Cannot create dir " + targetLocation.getAbsolutePath());
			}

			final String[] children = sourceLocation.list();
			if (children == null) {
				throw new IOException("Failed to list contents of directory " + sourceLocation.getAbsolutePath());
			}
			for (String child : children) {
				copy(new File(sourceLocation, child), new File(targetLocation, child));
			}
		} else {
			// ensure parent directory exists
			final File directory = targetLocation.getParentFile();
			if (directory != null && !directory.exists() && !directory.mkdirs()) {
				throw new IOException("Cannot create dir " + directory.getAbsolutePath());
			}

			final int BUFFER_SIZE = 1024;
			try (InputStream in = new FileInputStream(sourceLocation);
				 OutputStream out = new FileOutputStream(targetLocation)) {
				byte[] buf = new byte[BUFFER_SIZE];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
			}
		}
	}