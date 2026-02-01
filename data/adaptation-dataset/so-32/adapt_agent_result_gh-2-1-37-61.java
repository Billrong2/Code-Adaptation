/**
 * Returns the IPv4 address of the currently connected Wi‑Fi interface.
 * <p>
 * Wi‑Fi must be enabled and connected, and the application must hold the
 * appropriate permissions (e.g. ACCESS_WIFI_STATE). If the address cannot
 * be resolved, this method returns {@code null}.
 * </p>
 *
 * @return the Wi‑Fi IPv4 address as a string, or {@code null} if unavailable
 */
public String getWifiIpAddress() {
	if (context == null) {
		return null;
	}

	final WifiManager wifiManager;
	try {
		wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	} catch (SecurityException se) {
		MyLog.warn("Missing permission to access WIFI_SERVICE", se);
		return null;
	}

	if (wifiManager == null || wifiManager.getConnectionInfo() == null) {
		return null;
	}

	int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
	if (ipAddress == 0) {
		// Not connected or no IP assigned
		return null;
	}

	// Convert little-endian to big-endian if needed
	if (java.nio.ByteOrder.nativeOrder().equals(java.nio.ByteOrder.LITTLE_ENDIAN)) {
		ipAddress = Integer.reverseBytes(ipAddress);
	}

	// Normalize to 4 bytes to avoid sign-extension issues
	byte[] rawBytes = java.math.BigInteger.valueOf(ipAddress & 0xFFFFFFFFL).toByteArray();
	final byte[] ipByteArray = new byte[4];
	final int srcPos = Math.max(0, rawBytes.length - 4);
	final int length = Math.min(rawBytes.length, 4);
	System.arraycopy(rawBytes, srcPos, ipByteArray, 4 - length, length);

	try {
		return java.net.InetAddress.getByAddress(ipByteArray).getHostAddress();
	} catch (java.net.UnknownHostException ex) {
		MyLog.warn("Unable to resolve WiFi host address", ex);
		return null;
	}
}