/**
     * Counts the number of lines in a file by counting newline ('\n') characters.
     * <p>
     * If the file does not exist or cannot be opened, this method returns 0.
     * Only explicit newline characters are counted; a final line without a trailing
     * newline is not included in the count.
     * </p>
     *
     * @param filename absolute or relative path to the file
     * @return number of newline characters found, or 0 if the file does not exist
     */
    static int count(String filename) {
        if (filename == null) {
            return 0;
        }
        java.io.File file = new java.io.File(filename);
        if (!file.exists() || !file.isFile()) {
            return 0;
        }

        java.io.InputStream in = null;
        int lineCount = 0;
        try {
            in = new java.io.BufferedInputStream(new java.io.FileInputStream(file));
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                for (int i = 0; i < read; i++) {
                    if (buffer[i] == '\n') {
                        lineCount++;
                    }
                }
            }
        } catch (java.io.IOException e) {
            // On any I/O error, fall back to 0 as a safe default
            return 0;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (java.io.IOException e) {
                    // swallow close exception
                }
            }
        }
        return lineCount;
    }