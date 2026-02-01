public static void closeSilently(final Object... objects) {
        if (objects == null) {
            return;
        }
        for (final Object obj : objects) {
            if (obj == null) {
                continue;
            }
            try {
                android.util.Log.d(AIPROUTE_SHELL, "Attempting to close: " + obj.getClass().getName());
                if (obj instanceof java.io.Closeable) {
                    ((java.io.Closeable) obj).close();
                } else if (obj instanceof java.net.Socket) {
                    ((java.net.Socket) obj).close();
                } else if (obj instanceof java.net.DatagramSocket) {
                    ((java.net.DatagramSocket) obj).close();
                } else {
                    android.util.Log.e(AIPROUTE_SHELL, "cannot close: " + obj.getClass().getName());
                    // Trigger an exception to guarantee an error log, but swallow it
                    throw new RuntimeException("Unsupported close type: " + obj.getClass().getName());
                }
            } catch (Throwable t) {
                android.util.Log.e(AIPROUTE_SHELL, "Error while closing resource", t);
                // swallow all throwables to avoid crashes during cleanup
            }
        }
    }