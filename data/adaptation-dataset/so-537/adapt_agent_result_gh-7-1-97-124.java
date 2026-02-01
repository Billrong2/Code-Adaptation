/**
     * Adds the specified path to the JVM native library path at runtime.
     * <p>
     * This method uses reflection to access and mutate the private
     * {@code ClassLoader.usr_paths} field in order to append an additional
     * directory where native libraries can be found. The operation is JVM
     * specific and relies on internal implementation details.
     * </p>
     * <p>
     * Source inspiration: Stack Overflow solution demonstrating reflective
     * modification of {@code usr_paths} to extend {@code java.library.path}
     * at runtime.
     * </p>
     *
     * @param pathToAdd the absolute path to add to the native library search path
     * @throws Exception if reflective access fails or the JVM prevents modification
     */
    private static void addLibraryPath(final String pathToAdd) throws Exception {
        // Reject null or empty paths to avoid corrupting the library path.
        if (pathToAdd == null || pathToAdd.isEmpty()) {
            return;
        }

        // Reflectively access the internal ClassLoader usr_paths field.
        final Field usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");
        usrPathsField.setAccessible(true);

        final String[] paths = (String[]) usrPathsField.get(null);
        if (paths == null || paths.length == 0) {
            // Initialize with a single entry if the JVM provided no paths.
            usrPathsField.set(null, new String[] { pathToAdd });
            return;
        }

        // Check if the path is already present to avoid duplicates.
        for (final String path : paths) {
            if (pathToAdd.equals(path)) {
                return;
            }
        }

        // Append the new path to the existing array.
        final String[] newPaths = Arrays.copyOf(paths, paths.length + 1);
        newPaths[newPaths.length - 1] = pathToAdd;
        usrPathsField.set(null, newPaths);
    }