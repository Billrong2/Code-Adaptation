public static void copyJarResourcesRecursively(final File destDir, final JarURLConnection jarConnection) throws IOException {
    if (jarConnection == null) {
      throw new IOException("JarURLConnection must not be null");
    }
    if (destDir == null) {
      throw new IOException("Destination directory must not be null");
    }

    final String baseEntryName = jarConnection.getEntryName();
    final String normalizedBase = (baseEntryName == null || baseEntryName.length() == 0)
        ? ""
        : (baseEntryName.endsWith("/") ? baseEntryName : baseEntryName + "/");

    if (!FileUtils.ensureDirectoryExists(destDir)) {
      throw new IOException("Could not create destination directory: " + destDir);
    }

    try (JarFile jarFile = jarConnection.getJarFile()) {
      final Enumeration<JarEntry> entries = jarFile.entries();

      while (entries.hasMoreElements()) {
        final JarEntry entry = entries.nextElement();
        final String entryName = entry.getName();

        if (!entryName.startsWith(normalizedBase)) {
          continue;
        }

        final String relativePath = entryName.substring(normalizedBase.length());
        if (relativePath.length() == 0) {
          continue;
        }

        final File targetFile = new File(destDir, relativePath);

        if (entry.isDirectory()) {
          if (!FileUtils.ensureDirectoryExists(targetFile)) {
            throw new IOException("Could not create directory: " + targetFile);
          }
        } else {
          final File parentDir = targetFile.getParentFile();
          if (parentDir != null && !FileUtils.ensureDirectoryExists(parentDir)) {
            throw new IOException("Could not create directory: " + parentDir);
          }

          try (InputStream is = jarFile.getInputStream(entry)) {
            if (!FileUtils.copyStream(is, targetFile)) {
              throw new IOException("Failed to copy JAR entry to file: " + targetFile);
            }
          }
        }
      }
    }
  }