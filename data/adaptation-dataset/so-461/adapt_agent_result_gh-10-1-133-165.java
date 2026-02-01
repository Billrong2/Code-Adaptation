public static void zipDirectory(File directory, String prefixDir, ZipOutputStream zout) throws IOException {
        // Validate inputs
        if (directory == null || zout == null) {
            throw new IllegalArgumentException("directory and ZipOutputStream must not be null");
        }
        if (!directory.exists() || !directory.isDirectory()) {
            return;
        }

        // Normalize prefixDir
        String normalizedPrefix = prefixDir == null ? "" : prefixDir.trim();
        if (normalizedPrefix.length() > 0 && !normalizedPrefix.endsWith("/")) {
            normalizedPrefix = normalizedPrefix + "/";
        }
        if (normalizedPrefix.startsWith("/")) {
            normalizedPrefix = normalizedPrefix.substring(1);
        }

        URI baseUri = directory.toURI();
        java.util.Queue<File> queue = new LinkedList<File>();
        queue.add(directory);

        while (!queue.isEmpty()) {
            File currentDir = queue.remove();
            Log.d(TAG, "Zipping directory: " + currentDir.getAbsolutePath());

            File[] children = currentDir.listFiles();
            if (children == null) {
                continue;
            }

            for (File child : children) {
                String relativeName = baseUri.relativize(child.toURI()).getPath();
                String entryName = normalizedPrefix + relativeName;

                if (child.isDirectory()) {
                    // Ensure trailing slash for directory entries
                    if (!entryName.endsWith("/")) {
                        entryName = entryName + "/";
                    }
                    zout.putNextEntry(new ZipEntry(entryName));
                    zout.closeEntry();
                    queue.add(child);
                } else {
                    zout.putNextEntry(new ZipEntry(entryName));
                    FileInputStream fis = new FileInputStream(child);
                    try {
                        IOUtils.copy(fis, zout);
                    } finally {
                        fis.close();
                    }
                    zout.closeEntry();
                }
            }
        }
    }