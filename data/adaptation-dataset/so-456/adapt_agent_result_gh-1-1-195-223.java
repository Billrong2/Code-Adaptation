public static void uncompressTarGZ(File tarFile, File dest) throws IOException {
    final int BUFFER_SIZE = 8192;
    if (tarFile == null || dest == null) {
      throw new IllegalArgumentException("tarFile and dest must not be null");
    }
    if (!dest.exists() && !dest.mkdirs()) {
      throw new IOException("Could not create destination directory: " + dest);
    }

    final String destCanonicalPath = dest.getCanonicalPath() + File.separator;

    try (TarArchiveInputStream tarIn = new TarArchiveInputStream(
        new GzipCompressorInputStream(
            new BufferedInputStream(
                new FileInputStream(tarFile))))) {

      TarArchiveEntry tarEntry;
      while ((tarEntry = tarIn.getNextTarEntry()) != null) {
        File destPath = new File(dest, tarEntry.getName());

        // Guard against path traversal
        String destPathCanonical = destPath.getCanonicalPath();
        if (!destPathCanonical.startsWith(destCanonicalPath)) {
          throw new IOException("Blocked untrusted tar entry: " + tarEntry.getName());
        }

        if (tarEntry.isDirectory()) {
          if (!destPath.exists() && !destPath.mkdirs()) {
            throw new IOException("Could not create directory: " + destPath);
          }
          continue;
        }

        File parent = destPath.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
          throw new IOException("Could not create parent directories for: " + destPath);
        }

        // Set executable flag for cassandra before creation/writing
        if ("cassandra".equals(destPath.getName())) {
          destPath.setExecutable(true);
        }

        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(destPath))) {
          byte[] buffer = new byte[BUFFER_SIZE];
          int bytesRead;
          while ((bytesRead = tarIn.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
          }
        }
      }
    }
  }