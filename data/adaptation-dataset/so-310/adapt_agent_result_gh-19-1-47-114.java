/**
     * Unzips the given ZIP file into its parent directory ("unzip here").
     * <p>
     * The internal directory structure of the ZIP is preserved. Nested ZIP files
     * found during extraction are processed recursively using the same strategy.
     * </p>
     * <p>
     * I/O errors on individual entries are logged and do not stop the overall
     * extraction process.
     * </p>
     *
     * @param zipFilePath absolute or relative path to a ZIP file
     */
    public static void unzip(final String zipFilePath) {
        final int BUFFER_SIZE = 2048;

        if (zipFilePath == null) {
            LOG.warn("Zip file path is null, skipping unzip");
            return;
        }

        final File zipFile = new File(zipFilePath);
        if (!zipFile.exists() || !zipFile.isFile()) {
            LOG.warn("Zip file does not exist or is not a file: {}", zipFilePath);
            return;
        }

        final File targetDir = zipFile.getParentFile();
        if (targetDir == null) {
            LOG.warn("Cannot determine parent directory for zip: {}", zipFilePath);
            return;
        }

        ZipFile zip = null;
        try {
            zip = new ZipFile(zipFile);
            final Enumeration<? extends ZipEntry> entries = zip.entries();

            while (entries.hasMoreElements()) {
                final ZipEntry entry = entries.nextElement();
                final String entryName = entry.getName();

                try {
                    final File destFile = new File(targetDir, entryName);

                    // Zip Slip protection
                    final String destCanonicalPath = destFile.getCanonicalPath();
                    final String targetCanonicalPath = targetDir.getCanonicalPath() + File.separator;
                    if (!destCanonicalPath.startsWith(targetCanonicalPath)) {
                        LOG.error("Blocked zip entry outside target dir: {}", entryName);
                        continue;
                    }

                    if (entry.isDirectory()) {
                        if (!destFile.exists() && !destFile.mkdirs()) {
                            LOG.warn("Unable to create directory: {}", destFile.getAbsolutePath());
                        }
                        continue;
                    }

                    final File parentDir = destFile.getParentFile();
                    if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs()) {
                        LOG.warn("Unable to create parent directory: {}", parentDir.getAbsolutePath());
                    }

                    try (BufferedInputStream in = new BufferedInputStream(zip.getInputStream(entry));
                         BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(destFile), BUFFER_SIZE)) {

                        final byte[] buffer = new byte[BUFFER_SIZE];
                        int read;
                        while ((read = in.read(buffer)) != -1) {
                            out.write(buffer, 0, read);
                        }
                        out.flush();
                    }

                    if (entryName.toLowerCase().endsWith(".zip")) {
                        unzip(destFile.getAbsolutePath());
                    }
                } catch (IOException entryEx) {
                    LOG.error("Error extracting zip entry: {}", entryName, entryEx);
                }
            }
        } catch (IOException e) {
            LOG.error("Error opening zip file: {}", zipFilePath, e);
        } finally {
            if (zip != null) {
                try {
                    zip.close();
                } catch (IOException e) {
                    LOG.warn("Error closing zip file: {}", zipFilePath, e);
                }
            }
        }
    }