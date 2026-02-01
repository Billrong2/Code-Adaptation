/**
     * Check whether a TCP port on localhost is available.
     *
     * @param port the port number to test
     * @return {@code true} if the port is available, {@code false} if it is already in use
     */
    static boolean available(final int port)
    {
        java.net.Socket socket = null;
        try
        {
            socket = new java.net.Socket();
            socket.setReuseAddress(true);
            socket.connect(new java.net.InetSocketAddress("localhost", port));

            // If we connected successfully, something is listening on the port.
            getLogger().debug("Port " + port + " is not available");
            return false;
        }
        catch (java.io.IOException e)
        {
            // Connection failure implies the port is available.
            getLogger().debug("Port " + port + " is available", e);
            return true;
        }
        finally
        {
            if (socket != null)
            {
                try
                {
                    socket.close();
                }
                catch (java.io.IOException e)
                {
                    throw new RuntimeException("Failed to close socket while checking port availability", e);
                }
            }
        }
    }