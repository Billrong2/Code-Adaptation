public static boolean isPortAvailable(InetAddress localAddress, int port) {
        // Validate port range
        if (port < MIN_PORT_NUMBER || port > MAX_PORT_NUMBER) {
            throw new IllegalArgumentException("Invalid start port: " + port);
        }
        // Validate address (null means wildcard / any NIC)
        InetAddress bindAddress = localAddress;
        
        // Preliminary active check: try connecting as a client
        try (Socket client = new Socket()) {
            if (bindAddress != null) {
                client.connect(new java.net.InetSocketAddress(bindAddress, port), 200);
            } else {
                client.connect(new java.net.InetSocketAddress(port), 200);
            }
            // Connection succeeded, port is in use
            return false;
        } catch (IOException e) {
            // Expected when nothing is listening; proceed to bind-based check
            if (log.isTraceEnabled()) {
                log.trace("Active connect check failed for {}:{}; proceeding to bind test", bindAddress, port, e);
            }
        }

        // Fallback to bind-based availability check
        try (ServerSocket ss = (bindAddress != null ? new ServerSocket(port, 50, bindAddress) : new ServerSocket(port));
             DatagramSocket ds = (bindAddress != null ? new DatagramSocket(port, bindAddress) : new DatagramSocket(port))) {
            ss.setReuseAddress(true);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException e) {
            // Bind failed; port not available
            if (log.isTraceEnabled()) {
                log.trace("Bind-based port availability check failed for {}:{}", bindAddress, port, e);
            }
            return false;
        }
    }