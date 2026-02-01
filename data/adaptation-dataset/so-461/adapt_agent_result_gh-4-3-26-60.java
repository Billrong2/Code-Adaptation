public static void zip(final java.io.File directory, final java.io.File zipfile) throws java.io.IOException {
    final java.net.URI base = directory.toURI();
    final java.util.Deque<java.io.File> queue = new java.util.LinkedList<java.io.File>();
    queue.push(directory);
    java.util.zip.ZipOutputStream zout = null;
    try {
        final java.io.OutputStream out = new java.io.FileOutputStream(zipfile);
        zout = new java.util.zip.ZipOutputStream(out);
        while (!queue.isEmpty()) {
            final java.io.File currentDir = queue.pop();
            final java.io.File[] children = currentDir.listFiles();
            if (children == null) {
                continue;
            }
            for (final java.io.File kid : children) {
                String name = base.relativize(kid.toURI()).getPath();
                if (kid.isDirectory()) {
                    queue.push(kid);
                    name = name.endsWith("/") ? name : name + "/";
                    zout.putNextEntry(new java.util.zip.ZipEntry(name));
                } else {
                    zout.putNextEntry(new java.util.zip.ZipEntry(name));
                    java.nio.file.Files.copy(kid.toPath(), zout);
                    zout.closeEntry();
                }
            }
        }
    } finally {
        if (zout != null) {
            zout.close();
        }
    }
}