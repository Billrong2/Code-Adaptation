private Process startOtherProcess(final File mapFile) throws IOException {
    if (mapFile == null)
        throw new IllegalArgumentException("mapFile must not be null");
    if (!mapFile.exists())
        throw new IllegalArgumentException("mapFile does not exist: " + mapFile);

    final String javaHome = System.getProperty("java.home");
    final String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
    final String classpath = System.getProperty("java.class.path");
    final String mainClass = "net.openhft.chronicle.map.ExitHookTest";

    System.out.println("Resolved classpath: " + classpath);

    final ProcessBuilder builder = new ProcessBuilder(
            javaBin,
            "-cp",
            classpath,
            mainClass,
            mapFile.getAbsolutePath()
    ).inheritIO();

    return builder.start();
}