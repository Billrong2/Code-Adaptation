private File resourceToFile(final String resourcePath) {
    if (resourcePath == null || resourcePath.trim().isEmpty()) {
        throw new RuntimeException("resourcePath must not be null or empty");
    }

    final URL resourceUrl = getClass().getClassLoader().getResource(resourcePath);
    if (resourceUrl == null) {
        throw new RuntimeException("Resource not found on classpath: " + resourcePath);
    }

    File file;
    try {
        if (resourceUrl.toString().startsWith("jar:")) {
            try (InputStream input = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
                if (input == null) {
                    throw new RuntimeException("Unable to open InputStream for resource: " + resourcePath);
                }
                file = File.createTempFile("resource-", ".tmp");
                try (OutputStream out = new FileOutputStream(file)) {
                    byte[] buffer = new byte[4096];
                    int read;
                    while ((read = input.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                    }
                    out.flush();
                }
                file.deleteOnExit();
            }
        } else {
            file = new File(resourceUrl.getFile());
        }
    } catch (IOException e) {
        System.err.println("Failed to convert resource to file: " + resourcePath);
        e.printStackTrace(System.err);
        throw new RuntimeException(e);
    }

    if (file == null || !file.exists()) {
        throw new RuntimeException("Converted file does not exist for resource: " + resourcePath);
    }

    return file;
}