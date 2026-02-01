public static String getIPAddress(boolean useIPv4) {
	try {
		java.util.Enumeration<java.net.NetworkInterface> interfaces = java.net.NetworkInterface.getNetworkInterfaces();
		if (interfaces == null) {
			return "Unknown";
		}
		for (java.net.NetworkInterface intf : java.util.Collections.list(interfaces)) {
			if (intf == null) {
				continue;
			}
			java.util.Enumeration<java.net.InetAddress> addrs = intf.getInetAddresses();
			if (addrs == null) {
				continue;
			}
			for (java.net.InetAddress addr : java.util.Collections.list(addrs)) {
				if (addr == null || addr.isLoopbackAddress()) {
					continue;
				}
				String hostAddress = addr.getHostAddress();
				if (hostAddress == null) {
					continue;
				}
				boolean isIPv4 = org.apache.http.conn.util.InetAddressUtils.isIPv4Address(hostAddress);
				if (useIPv4) {
					if (isIPv4) {
						return hostAddress.toUpperCase(java.util.Locale.US);
					}
				} else {
					if (!isIPv4) {
						int zoneIndex = hostAddress.indexOf('%');
						String ipv6 = zoneIndex < 0 ? hostAddress : hostAddress.substring(0, zoneIndex);
						return ipv6.toUpperCase(java.util.Locale.US);
					}
				}
			}
		}
	} catch (Throwable t) {
		// Swallow all exceptions and fall through to return Unknown
	}
	return "Unknown";
}