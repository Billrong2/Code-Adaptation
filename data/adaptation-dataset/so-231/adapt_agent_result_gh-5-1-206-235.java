    /**
     * Recursively zips the contents of a directory into an already-open ZipOutputStream.
     * The directory structure is preserved by computing relative paths from basePathLength.
     *
     * @param out the target ZipOutputStream (not closed by this method)
     * @param directory the directory whose contents should be zipped
     * @param basePathLength the length of the base path used to compute relative entry names
     * @throws IOException if any I/O error occurs
     */
    private static void zipDirectoryContents(java.util.zip.ZipOutputStream out, java.io.File directory, int basePathLength) throws java.io.IOException {
        final int BUFFER_SIZE = 2048;

        if (out == null || directory == null) {
            return;
        }
        if (!directory.exists() || !directory.isDirectory() || !directory.canRead()) {
            return;
        }

        java.io.File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        byte[] buffer = new byte[BUFFER_SIZE];

        for (java.io.File file : files) {
            if (file == null) {
                continue;
            }
            if (file.isDirectory()) {
                zipDirectoryContents(out, file, basePathLength);
            } else {
                String fullPath = file.getAbsolutePath();
                if (fullPath.length() <= basePathLength) {
                    continue;
                }
                String entryName = fullPath.substring(basePathLength);
                if (entryName.startsWith(java.io.File.separator)) {
                    entryName = entryName.substring(1);
                }

                java.util.zip.ZipEntry entry = new java.util.zip.ZipEntry(entryName);
                out.putNextEntry(entry);

                try (java.io.BufferedInputStream in = new java.io.BufferedInputStream(new java.io.FileInputStream(file), BUFFER_SIZE)) {
                    int count;
                    while ((count = in.read(buffer)) != -1) {
                        out.write(buffer, 0, count);
                    }
                }
                out.closeEntry();
            }
        }
    }