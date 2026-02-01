public static void zip(String sourcePath, String destPath) throws IOException {
    if (sourcePath == null || sourcePath.length() == 0) {
        throw new IllegalArgumentException("sourcePath is null or empty");
    }
    if (destPath == null || destPath.length() == 0) {
        throw new IllegalArgumentException("destPath is null or empty");
    }

    final int BUFFER = 2048;
    File sourceFile = new File(sourcePath);

    try (FileOutputStream dest = new FileOutputStream(destPath);
         ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest))) {

        if (sourceFile.isDirectory()) {
            File parent = sourceFile.getParentFile();
            int basePathLength = parent != null ? parent.getPath().length() + 1 : sourceFile.getPath().length() + 1;
            zipSubFolder(out, sourceFile, basePathLength);
        } else {
            byte[] data = new byte[BUFFER];
            try (FileInputStream fi = new FileInputStream(sourceFile);
                 BufferedInputStream origin = new BufferedInputStream(fi, BUFFER)) {

                String path = sourceFile.getPath();
                int lastSeparator = path.lastIndexOf(File.separatorChar);
                String entryName = lastSeparator >= 0 ? path.substring(lastSeparator + 1) : path;

                ZipEntry entry = new ZipEntry(entryName);
                out.putNextEntry(entry);

                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                out.closeEntry();
            }
        }
    }
}