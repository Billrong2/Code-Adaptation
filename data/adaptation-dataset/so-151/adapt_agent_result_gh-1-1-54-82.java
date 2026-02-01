private File resourceToFile(final String resourcePath) {
    if (resourcePath == null || resourcePath.isEmpty()) {
        throw new IllegalArgumentException("resourcePath must not be null or empty");
    }

    File file = null;
    final URL res = getClass().getResource(resourcePath.startsWith("/") ? resourcePath : "/" + resourcePath);
    if (res == null) {
        throw new RuntimeException("Error: Resource " + resourcePath + " not found!");
    }

    try {
        if ("jar".equals(res.getProtocol())) {
            try (InputStream input = getClass().getResourceAsStream(resourcePath.startsWith("/") ? resourcePath : "/" + resourcePath)) {
                if (input == null) {
                    throw new RuntimeException("Error: Resource stream " + resourcePath + " not found!");
                }
                file = File.createTempFile("tempfile", ".tmp");
                try (OutputStream out = new FileOutputStream(file)) {
                    final byte[] buffer = new byte[1024];
                    int read;
                    while ((read = input.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                    }
                }
                file.deleteOnExit();
            }
        } else {
            file = new File(res.getFile());
        }
    } catch (IOException ex) {
        ex.printStackTrace(System.err);
        throw new RuntimeException(ex);
    }

    if (file == null || !file.exists()) {
        throw new RuntimeException("Error: File " + file + " not found!");
    }

    return file;
}