static public void addLibraryPath(String pathToAdd) {
    if (pathToAdd == null) {
        Status.print("Cannot add library path");
        return;
    }

    try {
        final Field usrPaths = ClassLoader.class.getDeclaredField("usr_paths");
        usrPaths.setAccessible(true);

        final Object value = usrPaths.get(null);
        if (!(value instanceof String[])) {
            Status.print("Cannot add library path");
            return;
        }

        final String[] paths = (String[]) value;
        if (paths == null) {
            Status.print("Cannot add library path");
            return;
        }

        final String[] newPaths = Arrays.copyOf(paths, paths.length + 1);
        newPaths[newPaths.length - 1] = pathToAdd;
        usrPaths.set(null, newPaths);
    } catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException e) {
        e.printStackTrace();
        Status.print("Cannot add library path");
    }
}