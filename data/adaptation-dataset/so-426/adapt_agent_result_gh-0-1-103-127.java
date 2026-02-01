@Thunk static String getWifiIpAddress() {
	// Adapted from a StackOverflow answer (source ID to be added)
	try {
		final java.util.Enumeration<java.net.NetworkInterface> interfaces = java.net.NetworkInterface.getNetworkInterfaces();
		while (interfaces != null && interfaces.hasMoreElements()) {
			final java.net.NetworkInterface intf = interfaces.nextElement();
			if (intf != null && intf.getName() != null && intf.getName().contains("wlan")) {
				final java.util.Enumeration<java.net.InetAddress> addresses = intf.getInetAddresses();
				while (addresses != null && addresses.hasMoreElements()) {
					final java.net.InetAddress inetAddress = addresses.nextElement();
					if (inetAddress != null
							&& !inetAddress.isLoopbackAddress()
							&& inetAddress.getAddress() != null
							&& inetAddress.getAddress().length == 4) { // IPv4
						return inetAddress.getHostAddress();
					}
				}
			}
		}
	} catch (final Exception e) {
		com.painless.pc.singleton.Debug.log(e);
	}
	return "0.0.0.0";
}