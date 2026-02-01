/**
     * Counts the number of newline characters ('\n') in the given file.
     * <p>
     * If the file does not exist, this method returns {@code 0}.
     * Only explicit newline characters are counted; a non-empty file without
     * any '\n' characters will return {@code 0}.
     *
     * @param filename absolute or relative path to the file
     * @return the number of '\n' characters found in the file, or {@code 0} if the file does not exist
     * @throws IOException if an I/O error occurs while reading the file
     */
    static int count(String filename) throws IOException {
        if (filename == null)
            return 0;

        java.io.File file = new java.io.File(filename);
        if (!file.exists())
            return 0;

        java.io.InputStream is = new java.io.BufferedInputStream(new java.io.FileInputStream(file));
        try {
            byte[] buffer = new byte[1024];
            int count = 0;
            int readChars;
            while ((readChars = is.read(buffer)) != -1) {
                for (int i = 0; i < readChars; ++i) {
                    if (buffer[i] == '\n') {
                        ++count;
                    }
                }
            }
            return count;
        } finally {
            try {
                is.close();
            } catch (java.io.IOException e) {
                // swallow exception on close
            }
        }
    }