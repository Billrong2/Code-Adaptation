private static void zipSubFolder(ZipOutputStream out, File folder, String relativePath) throws IOException {
    final int BUFFER = 2048;

    if (out == null || folder == null) {
        return;
    }
    if (!folder.exists() || !folder.isDirectory()) {
        return;
    }

    File[] fileList = folder.listFiles();
    if (fileList == null) {
        return;
    }

    // Normalize the relative path so it is either empty or ends with '/'
    String basePath = (relativePath == null || relativePath.length() == 0)
            ? ""
            : (relativePath.endsWith("/") ? relativePath : relativePath + "/");

    for (File file : fileList) {
        if (file == null || !file.exists()) {
            continue;
        }

        if (file.isDirectory()) {
            // Recurse into subdirectory with updated relative path
            zipSubFolder(out, file, basePath + file.getName());
        } else {
            String entryName = basePath + file.getName();
            byte[] buffer = new byte[BUFFER];

            try (BufferedInputStream origin = new BufferedInputStream(new FileInputStream(file), BUFFER)) {
                ZipEntry entry = new ZipEntry(entryName);
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(buffer)) != -1) {
                    out.write(buffer, 0, count);
                }
                out.closeEntry();
            }
        }
    }
  }