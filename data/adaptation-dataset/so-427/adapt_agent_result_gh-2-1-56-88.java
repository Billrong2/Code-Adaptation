/**
   * DOCUMENT ME!
   *
   * @param toCopy DOCUMENT ME!
   * @param destDir DOCUMENT ME!
   * @return DOCUMENT ME!
   */
  public static boolean copyFilesRecusively(final java.io.File toCopy, final java.io.File destDir) {
    assert destDir.isDirectory();

    if (!toCopy.isDirectory()) {
      return FileUtils.copyFile(toCopy, new java.io.File(destDir, toCopy.getName()));
    } else {
      final java.io.File newDestDir = new java.io.File(destDir, toCopy.getName());

      if (!newDestDir.exists() && !newDestDir.mkdir()) {
        return false;
      }

      final java.io.File[] children = toCopy.listFiles();

      for (int i = 0; i < children.length; i++) {
        final java.io.File child = children[i];

        if (!FileUtils.copyFilesRecusively(child, newDestDir)) {
          return false;
        }
      }
    }

    return true;
  }