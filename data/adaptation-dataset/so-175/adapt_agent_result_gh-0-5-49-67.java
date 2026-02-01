public static void unzip(File zipfile, File directory) throws IOException {
    if (zipfile == null || directory == null) {
        throw new IllegalArgumentException("zipfile and directory must not be null");
    }
    if (!zipfile.exists() || !zipfile.isFile()) {
        throw new FileNotFoundException("Zip file does not exist or is not a file: " + zipfile);
    }
    if (!directory.exists()) {
        if (!directory.mkdirs()) {
            throw new IOException("Could not create target directory: " + directory);
        }
    }
    File canonicalTargetDir = directory.getCanonicalFile();
    try (ZipFile zipFile = new ZipFile(zipfile)) {
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            File outFile = new File(directory, entry.getName());
            File canonicalOutFile = outFile.getCanonicalFile();
            if (!canonicalOutFile.getPath().startsWith(canonicalTargetDir.getPath() + File.separator)) {
                throw new IOException("Blocked Zip Slip attempt: " + entry.getName());
            }
            if (entry.isDirectory()) {
                if (!canonicalOutFile.exists() && !canonicalOutFile.mkdirs()) {
                    throw new IOException("Could not create directory: " + canonicalOutFile);
                }
            } else {
                File parent = canonicalOutFile.getParentFile();
                if (parent != null && !parent.exists() && !parent.mkdirs()) {
                    throw new IOException("Could not create parent directory: " + parent);
                }
                try (InputStream in = zipFile.getInputStream(entry)) {
                    copy(in, canonicalOutFile);
                }
            }
        }
    }
}