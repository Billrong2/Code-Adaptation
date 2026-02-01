public static void zipToStream(final File[] files, final OutputStream outputStream) throws IOException {
        if (files == null) {
            throw new IllegalArgumentException("files must not be null");
        }
        if (outputStream == null) {
            throw new IllegalArgumentException("outputStream must not be null");
        }
        final int BUFFER_SIZE = 2048;
        try (ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(outputStream))) {
            byte[] buffer = new byte[BUFFER_SIZE];
            for (File file : files) {
                if (file == null) {
                    continue;
                }
                if (!file.exists() || !file.canRead()) {
                    throw new IOException("Cannot read file: " + file);
                }
                if (file.isDirectory()) {
                    // TRICKY: add 1 to exclude the leading path separator
                    int basePathLength = file.getAbsolutePath().length() + 1;
                    zipSubFolder(zipOut, file, basePathLength);
                } else {
                    try (BufferedInputStream origin = new BufferedInputStream(new FileInputStream(file), BUFFER_SIZE)) {
                        ZipEntry entry = new ZipEntry(file.getName());
                        zipOut.putNextEntry(entry);
                        int count;
                        while ((count = origin.read(buffer, 0, BUFFER_SIZE)) != -1) {
                            zipOut.write(buffer, 0, count);
                        }
                        zipOut.closeEntry();
                    }
                }
            }
        }
    }