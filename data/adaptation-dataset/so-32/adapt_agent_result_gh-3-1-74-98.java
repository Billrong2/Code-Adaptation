/* Source adapted from a Stack Overflow answer on converting Wi-Fi IP address to string. */
protected String getWifiIpAddress(android.content.Context context) {
    if (context == null) {
        return null;
    }

    android.net.wifi.WifiManager wifiManager = (android.net.wifi.WifiManager) context.getSystemService(android.content.Context.WIFI_SERVICE);
    if (wifiManager == null || wifiManager.getConnectionInfo() == null) {
        return null;
    }

    int ipAddress = wifiManager.getConnectionInfo().getIpAddress();

    // Convert little-endian to big-endian if needed
    if (java.nio.ByteOrder.nativeOrder().equals(java.nio.ByteOrder.LITTLE_ENDIAN)) {
        ipAddress = java.lang.Integer.reverseBytes(ipAddress);
    }

    byte[] ipByteArray = java.math.BigInteger.valueOf(ipAddress).toByteArray();

    String ipAddressString;
    try {
        ipAddressString = java.net.InetAddress.getByAddress(ipByteArray).getHostAddress();
    } catch (java.net.UnknownHostException ex) {
        android.util.Log.e(TAG, "Unable to get host address.", ex);
        ipAddressString = null;
    }

    return ipAddressString;
}