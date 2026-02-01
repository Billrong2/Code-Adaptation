    /**
     * Unpack a ZIP archive into the given preset directory.
     * <p>
     * Source: https://stackoverflow.com/questions/3382996/how-to-unzip-files-programmatically-in-android
     * </p>
     *
     * @param presetDir the target directory where the ZIP contents will be extracted (must end with a file separator)
     * @param zipname   the name of the ZIP file located in {@code presetDir}
     * @return {@code true} if the ZIP was successfully unpacked, {@code false} otherwise
     */
    private boolean unpackZip(final String presetDir, final String zipname) {
        if (presetDir == null || presetDir.length() == 0 || zipname == null || zipname.length() == 0) {
            return false;
        }

        final File baseDir = new File(presetDir);
        try (InputStream is = new FileInputStream(new File(baseDir, zipname));
             ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is))) {

            ZipEntry ze;
            final byte[] buffer = new byte[1024];

            final String baseCanonicalPath = baseDir.getCanonicalPath();

            while ((ze = zis.getNextEntry()) != null) {
                final String entryName = ze.getName();
                if (entryName == null || entryName.length() == 0) {
                    zis.closeEntry();
                    continue;
                }

                final File outFile = new File(baseDir, entryName);
                final String outCanonicalPath = outFile.getCanonicalPath();
                // Guard against directory traversal ("../")
                if (!outCanonicalPath.startsWith(baseCanonicalPath)) {
                    zis.closeEntry();
                    continue;
                }

                if (ze.isDirectory()) {
                    if (!outFile.exists() && !outFile.mkdirs()) {
                        return false;
                    }
                    zis.closeEntry();
                    continue;
                }

                File parent = outFile.getParentFile();
                if (parent != null && !parent.exists() && !parent.mkdirs()) {
                    return false;
                }

                try (FileOutputStream fout = new FileOutputStream(outFile)) {
                    int count;
                    while ((count = zis.read(buffer)) != -1) {
                        fout.write(buffer, 0, count);
                    }
                }
                zis.closeEntry();
            }
        } catch (java.io.FileNotFoundException e) {
            Log.w("PresetEditorActivity", "ZIP file not found: " + zipname, e);
            return false;
        } catch (IOException e) {
            Log.w("PresetEditorActivity", "Error unpacking ZIP file: " + zipname, e);
            return false;
        }
        return true;
    }