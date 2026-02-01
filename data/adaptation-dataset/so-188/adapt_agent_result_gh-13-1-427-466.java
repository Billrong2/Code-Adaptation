/**
	 * Recursively copies a file or directory from {@code sourceLocation} to {@code targetLocation}.
	 * <p>
	 * If the source is a directory, all children are copied recursively. The target directory
	 * is created automatically if it does not already exist.
	 * </p>
	 *
	 * @param sourceLocation the source file or directory to copy
	 * @param targetLocation the destination file or directory
	 * @throws java.io.IOException if an I/O error occurs or a required directory cannot be created
	 */
	public static void copy(final java.io.File sourceLocation,
			final java.io.File targetLocation) throws java.io.IOException {

		if (sourceLocation.isDirectory()) {
			if (!targetLocation.exists() && !targetLocation.mkdirs()) {
				throw new java.io.IOException("Cannot create dir "
						+ targetLocation.getAbsolutePath());
			}

			String[] children = sourceLocation.list();
			if (children == null) {
				return; // nothing to copy or I/O error occurred
			}
			for (int i = 0; i < children.length; i++) {
				copy(new java.io.File(sourceLocation, children[i]),
						new java.io.File(targetLocation, children[i]));
			}
		} else {
			// make sure the directory we plan to store the file in exists
			java.io.File directory = targetLocation.getParentFile();
			if (directory != null && !directory.exists() && !directory.mkdirs()) {
				throw new java.io.IOException("Cannot create dir "
						+ directory.getAbsolutePath());
			}

			java.io.InputStream in = null;
			java.io.OutputStream out = null;
			try {
				in = new java.io.FileInputStream(sourceLocation);
				out = new java.io.FileOutputStream(targetLocation);
				copyFromInToOut(in, out);
			} finally {
				if (out != null) {
					out.close();
				}
			}
		}
	}