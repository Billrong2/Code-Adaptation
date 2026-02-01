public static String getIPAddress(boolean useIPv4) {
    try {
        java.util.Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        if (interfaces == null) {
            return "";
        }
        for (NetworkInterface intf : java.util.Collections.list(interfaces)) {
            try {
                if (intf == null || !intf.isUp() || intf.isLoopback()) {
                    continue;
                }
            } catch (Exception ignore) {
                continue;
            }
            java.util.Enumeration<InetAddress> addresses = intf.getInetAddresses();
            if (addresses == null) {
                continue;
            }
            for (InetAddress addr : java.util.Collections.list(addresses)) {
                if (addr == null || addr.isLoopbackAddress()) {
                    continue;
                }
                String hostAddress = addr.getHostAddress();
                if (hostAddress == null || hostAddress.length() == 0) {
                    continue;
                }
                boolean isIPv4 = InetAddressUtils.isIPv4Address(hostAddress);
                if (useIPv4) {
                    if (isIPv4) {
                        return hostAddress;
                    }
                } else {
                    if (!isIPv4) {
                        int scopeIndex = hostAddress.indexOf('%');
                        if (scopeIndex >= 0) {
                            hostAddress = hostAddress.substring(0, scopeIndex);
                        }
                        return hostAddress.toUpperCase();
                    }
                }
            }
        }
    } catch (Exception e) {
        // ignore and fall through to empty result
    }
    return "";
}