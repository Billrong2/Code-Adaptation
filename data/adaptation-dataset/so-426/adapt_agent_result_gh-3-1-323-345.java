private String getWifiIp() {
    try {
        java.util.Enumeration<java.net.NetworkInterface> interfaces = java.net.NetworkInterface.getNetworkInterfaces();
        while (interfaces != null && interfaces.hasMoreElements()) {
            java.net.NetworkInterface networkInterface = interfaces.nextElement();
            if (networkInterface.getName() != null && networkInterface.getName().contains("wlan")) {
                java.util.Enumeration<java.net.InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses != null && addresses.hasMoreElements()) {
                    java.net.InetAddress inetAddress = addresses.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress.getAddress() != null && inetAddress.getAddress().length == 4) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        }
    } catch (java.net.SocketException e) {
        return null;
    }
    return null;
}