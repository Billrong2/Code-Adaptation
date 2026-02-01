public static void unzip(String zipFile) {
        if (zipFile == null) {
            return;
        }
        final int BUFFER = 2048;
        java.io.File file = new java.io.File(zipFile);
        if (!file.exists()) {
            LOG.error("Zip file does not exist: {}", zipFile);
            return;
        }
        java.util.zip.ZipFile zip = null;
        try {
            zip = new java.util.zip.ZipFile(file);
            java.io.File targetDir = file.getParentFile();
            if (targetDir == null) {
                targetDir = new java.io.File(".");
            }
            java.util.Enumeration<? extends java.util.zip.ZipEntry> zipFileEntries = zip.entries();
            while (zipFileEntries.hasMoreElements()) {
                java.util.zip.ZipEntry entry = zipFileEntries.nextElement();
                String currentEntry = entry.getName();
                java.io.File destFile = new java.io.File(targetDir, currentEntry);
                java.io.File destinationParent = destFile.getParentFile();
                if (destinationParent != null && !destinationParent.exists()) {
                    if (!destinationParent.mkdirs() && !destinationParent.exists()) {
                        LOG.error("Failed to create directory: {}", destinationParent.getAbsolutePath());
                        continue;
                    }
                }
                java.io.BufferedInputStream is = null;
                java.io.BufferedOutputStream dest = null;
                java.io.FileOutputStream fos = null;
                try {
                    if (!entry.isDirectory()) {
                        is = new java.io.BufferedInputStream(zip.getInputStream(entry));
                        fos = new java.io.FileOutputStream(destFile);
                        dest = new java.io.BufferedOutputStream(fos, BUFFER);
                        byte[] data = new byte[BUFFER];
                        int currentByte;
                        while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                            dest.write(data, 0, currentByte);
                        }
                    }
                } catch (java.io.IOException ioe) {
                    LOG.error("I/O error while extracting entry: {}", currentEntry, ioe);
                } finally {
                    try {
                        if (dest != null) {
                            dest.flush();
                            dest.close();
                        }
                    } catch (java.io.IOException ioe) {
                        LOG.error("Error closing output stream for entry: {}", currentEntry, ioe);
                    }
                    try {
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (java.io.IOException ioe) {
                        LOG.error("Error closing file output stream for entry: {}", currentEntry, ioe);
                    }
                    try {
                        if (is != null) {
                            is.close();
                        }
                    } catch (java.io.IOException ioe) {
                        LOG.error("Error closing input stream for entry: {}", currentEntry, ioe);
                    }
                }
                if (currentEntry != null && currentEntry.endsWith(".zip") && destFile.exists()) {
                    unzip(destFile.getAbsolutePath());
                }
            }
        } catch (java.io.IOException ioe) {
            LOG.error("Error opening zip file: {}", zipFile, ioe);
        } finally {
            if (zip != null) {
                try {
                    zip.close();
                } catch (java.io.IOException ioe) {
                    LOG.error("Error closing zip file: {}", zipFile, ioe);
                }
            }
        }
    }