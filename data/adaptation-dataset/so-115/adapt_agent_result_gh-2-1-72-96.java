@Override
public void uncaughtException(Thread t, Throwable e) {
    final String timestamp = String.valueOf(System.currentTimeMillis());
    final String filename = timestamp + "." + STACKTRACE_EXT;

    // Build stacktrace string safely
    String stacktrace = "";
    try (Writer result = new StringWriter(); PrintWriter printWriter = new PrintWriter(result)) {
        if (e != null) {
            e.printStackTrace(printWriter);
        }
        stacktrace = result.toString();
    } catch (Exception ioEx) {
        ioEx.printStackTrace();
    }

    // Write stacktrace only if a directory is configured
    if (mStracktraceDir != null) {
        try {
            writeToFile(stacktrace, filename);
        } catch (Exception writeEx) {
            writeEx.printStackTrace();
        }
    }

    // Delegate to default handler
    if (defaultUEH != null) {
        defaultUEH.uncaughtException(t, e);
    }

    // Ensure the process is terminated to avoid inconsistent state
    android.os.Process.killProcess(android.os.Process.myPid());
    System.exit(10);
}