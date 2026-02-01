/**
 * Checks to see if a specific port is available.
 * <p>
 * Adapted from a Stack Overflow answer.
 * </p>
 *
 * @param port the port to check for availability
 * @return true if the port is available; false otherwise
 */
public static boolean available(int port) {
    if (port < 1 || port > 65535) {
        throw new IllegalArgumentException("Invalid start port: " + port);
    }

    ServerSocket ss = null;
    DatagramSocket ds = null;
    try {
        ss = new ServerSocket(port);
        ss.setReuseAddress(true);
        ds = new DatagramSocket(port);
        ds.setReuseAddress(true);
        return true;
    } catch (IOException e) {
    } finally {
        if (ds != null) {
            ds.close();
        }

        if (ss != null) {
            try {
                ss.close();
            } catch (IOException e) {
                /* should not be thrown */
            }
        }
    }

    return false;
}