private static void zipSubDirectory(android.content.Context context, String basePath, java.io.File dir, java.util.zip.ZipOutputStream zout) throws java.io.IOException {
    if (dir == null || zout == null) {
      return;
    }

    final byte[] buffer = new byte[4096];
    final java.io.File[] files = dir.listFiles();

    if (files == null) {
      return;
    }

    for (java.io.File file : files) {
      if (file == null) {
        continue;
      }

      if (file.isDirectory()) {
        String path = basePath + file.getName() + "/";
        zout.putNextEntry(new java.util.zip.ZipEntry(path));
        zipSubDirectory(context, path, file, zout);
        zout.closeEntry();
      }
      else {
        if (context != null) {
          android.media.MediaScannerConnection.scanFile(
            context,
            new String[]{file.getAbsolutePath()},
            null,
            null);
        }

        zout.putNextEntry(new java.util.zip.ZipEntry(basePath + file.getName()));

        try (java.io.FileInputStream fin = new java.io.FileInputStream(file)) {
          int length;
          while ((length = fin.read(buffer)) > 0) {
            zout.write(buffer, 0, length);
          }
        }

        zout.closeEntry();
      }
    }
  }