    /**
     * Get IP address from first non-loopback interface.
     *
     * @param useIPv4 true to return IPv4, false to return IPv6
     * @return IP address as string or empty string on error/not found
     */
    public static String getIPAddress(boolean useIPv4) {
        try {
            java.util.List<java.net.NetworkInterface> interfaces = java.util.Collections
                    .list(java.net.NetworkInterface.getNetworkInterfaces());
            if (interfaces == null || interfaces.isEmpty()) {
                return "";
            }
            for (java.net.NetworkInterface intf : interfaces) {
                if (intf == null) {
                    continue;
                }
                try {
                    if (!intf.isUp() || intf.isLoopback() || intf.isVirtual()) {
                        continue;
                    }
                } catch (java.net.SocketException ignore) {
                    continue;
                }
                java.util.Enumeration<java.net.InetAddress> addrs = intf.getInetAddresses();
                if (addrs == null) {
                    continue;
                }
                while (addrs.hasMoreElements()) {
                    java.net.InetAddress addr = addrs.nextElement();
                    if (addr == null || addr.isLoopbackAddress()) {
                        continue;
                    }
                    String hostAddress = addr.getHostAddress();
                    if (hostAddress == null || hostAddress.length() == 0) {
                        continue;
                    }
                    boolean isIPv4 = org.apache.http.conn.util.InetAddressUtils.isIPv4Address(hostAddress);
                    if (useIPv4) {
                        if (isIPv4) {
                            return hostAddress;
                        }
                    } else {
                        if (!isIPv4 && org.apache.http.conn.util.InetAddressUtils.isIPv6Address(hostAddress)) {
                            int delim = hostAddress.indexOf('%');
                            return (delim < 0) ? hostAddress : hostAddress.substring(0, delim);
                        }
                    }
                }
            }
        } catch (java.net.SocketException e) {
            // swallow and fall through
        }
        return "";
    }