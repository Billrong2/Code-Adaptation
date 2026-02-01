private static void zipDirectory(File directory, String prefix, ZipOutputStream zout) throws IOException {
    if (directory == null || zout == null || prefix == null) {
        throw new IllegalArgumentException("directory, prefix, and ZipOutputStream must not be null");
    }
    if (!directory.exists() || !directory.isDirectory()) {
        throw new IllegalArgumentException("directory must exist and be a directory: " + directory);
    }

    java.net.URI baseUri = directory.toURI();
    java.util.LinkedList<File> queue = new java.util.LinkedList<File>();
    queue.add(directory); // FIFO for BFS

    while (!queue.isEmpty()) {
        File currentDir = queue.poll();
        File[] children = currentDir.listFiles();
        if (children == null) {
            continue;
        }
        for (File child : children) {
            String relativePath = baseUri.relativize(child.toURI()).getPath();
            String entryName = prefix + relativePath;
            if (child.isDirectory()) {
                queue.add(child);
                if (!entryName.endsWith("/")) {
                    entryName = entryName + "/";
                }
                zout.putNextEntry(new java.util.zip.ZipEntry(entryName));
                zout.closeEntry();
            } else {
                java.io.FileInputStream in = null;
                zout.putNextEntry(new java.util.zip.ZipEntry(entryName));
                try {
                    in = new java.io.FileInputStream(child);
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int count;
                    while ((count = in.read(buffer)) != -1) {
                        zout.write(buffer, 0, count);
                    }
                } finally {
                    try {
                        if (in != null) {
                            in.close();
                        }
                    } finally {
                        zout.closeEntry();
                    }
                }
            }
        }
    }
}