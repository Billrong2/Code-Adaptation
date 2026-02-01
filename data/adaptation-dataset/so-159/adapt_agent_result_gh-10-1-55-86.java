public static String getIPAddress(final boolean useIPv4) throws SocketException {
	final java.util.Enumeration<java.net.NetworkInterface> interfacesEnum = java.net.NetworkInterface.getNetworkInterfaces();
	if (interfacesEnum == null) return "";
	for (final java.net.NetworkInterface intf : java.util.Collections.list(interfacesEnum)) {
		// Skip down or loopback interfaces
		if (intf == null || !intf.isUp() || intf.isLoopback()) continue;
		final java.util.Enumeration<java.net.InetAddress> addrsEnum = intf.getInetAddresses();
		if (addrsEnum == null) continue;
		for (final java.net.InetAddress addr : java.util.Collections.list(addrsEnum)) {
			if (addr == null || addr.isLoopbackAddress()) continue;
			final String hostAddress = addr.getHostAddress();
			if (hostAddress == null || hostAddress.length() == 0) continue;
			final boolean isIPv4 = org.apache.http.conn.util.InetAddressUtils.isIPv4Address(hostAddress);
			if (useIPv4) {
				if (isIPv4) return hostAddress;
			} else {
				if (!isIPv4) {
					// Strip IPv6 scope suffix (e.g., "%wlan0")
					final int delim = hostAddress.indexOf('%');
					return (delim < 0) ? hostAddress : hostAddress.substring(0, delim);
				}
			}
		}
	}
	return "";
}