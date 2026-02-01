public static String getIPAddress(boolean useIPv4) {
    try {
        java.util.Enumeration<java.net.NetworkInterface> interfaces = java.net.NetworkInterface.getNetworkInterfaces();
        if (interfaces == null) return "";
        for (java.net.NetworkInterface intf : java.util.Collections.list(interfaces)) {
            if (intf == null) continue;
            java.util.Enumeration<java.net.InetAddress> addrs = intf.getInetAddresses();
            if (addrs == null) continue;
            for (java.net.InetAddress addr : java.util.Collections.list(addrs)) {
                if (addr == null || addr.isLoopbackAddress()) continue;
                String sAddr = addr.getHostAddress();
                if (sAddr == null) continue;
                sAddr = sAddr.trim();
                boolean isIPv4 = sAddr.indexOf(':') < 0;
                if (useIPv4) {
                    if (isIPv4) {
                        return sAddr;
                    }
                } else {
                    if (!isIPv4) {
                        int delim = sAddr.indexOf('%');
                        if (delim >= 0) {
                            sAddr = sAddr.substring(0, delim);
                        }
                        return sAddr.toUpperCase();
                    }
                }
            }
        }
    } catch (Exception ignored) {
        // Swallow exceptions and fall through
    }
    return "";
}