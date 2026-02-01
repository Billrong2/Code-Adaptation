    /**
     * Sends a SIGQUIT (QUIT) signal to the given UNIX process using its native PID.
     * This method is UNIX-only and relies on reflective access to the internal pid field.
     */
    void killQuitProcess(Process process) throws IllegalArgumentException, NoSuchFieldException, IllegalAccessException, IOException, InterruptedException {
        if (process == null) {
            throw new IllegalArgumentException("Process must not be null");
        }
        if (!"java.lang.UNIXProcess".equals(process.getClass().getName())) {
            throw new IllegalArgumentException("Needs to be a UNIXProcess");
        }

        final Class<?> processClass = process.getClass();
        final java.lang.reflect.Field pidField = processClass.getDeclaredField("pid");
        pidField.setAccessible(true);
        final Object pidObject = pidField.get(process);
        final int pid = ((Integer) pidObject).intValue();

        Process killProcess = null;
        try {
            killProcess = Runtime.getRuntime().exec(new String[] { "/bin/kill", "-QUIT", String.valueOf(pid) });
            killProcess.waitFor();
        } finally {
            if (killProcess != null) {
                try {
                    killProcess.getInputStream().close();
                } catch (IOException ignored) {
                }
                try {
                    killProcess.getErrorStream().close();
                } catch (IOException ignored) {
                }
                try {
                    killProcess.getOutputStream().close();
                } catch (IOException ignored) {
                }
            }
        }
    }