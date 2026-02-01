/**
     * Returns the last non-loopback IP address found on an active network interface.
     * <p>
     * Adapted from a Stack Overflow answer; iterates available network interfaces and
     * captures an IP address without emitting output.
     *
     * @return the last IP address found, or null if none are available
     */
    public static String getIpAddress() {
        String ipAddress = null;
        try {
            final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces != null && interfaces.hasMoreElements()) {
                final NetworkInterface iface = interfaces.nextElement();
                // filter out loopback and inactive interfaces
                if (iface.isLoopback() || !iface.isUp()) {
                    continue;
                }
                final Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses != null && addresses.hasMoreElements()) {
                    final InetAddress addr = addresses.nextElement();
                    ipAddress = addr.getHostAddress();
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        return ipAddress;
    }