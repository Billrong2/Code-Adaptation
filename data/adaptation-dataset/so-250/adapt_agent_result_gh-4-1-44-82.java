@android.annotation.TargetApi(android.os.Build.VERSION_CODES.GINGERBREAD)
public static String getMACAddress(String interfaceName) {
    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.GINGERBREAD) {
        throw new IllegalStateException("API level < Gingerbread is not supported");
    }
    try {
        java.util.List<java.net.NetworkInterface> interfaces = java.util.Collections.list(java.net.NetworkInterface.getNetworkInterfaces());
        for (java.net.NetworkInterface intf : interfaces) {
            if (intf == null) continue;
            if (interfaceName != null && !interfaceName.equals(intf.getName())) continue;
            byte[] mac = intf.getHardwareAddress();
            if (mac == null || mac.length == 0) {
                if (interfaceName != null) return "";
                continue;
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X", mac[i]));
                if (i < mac.length - 1) sb.append(':');
            }
            return sb.toString();
        }
    } catch (java.net.SocketException e) {
        return "";
    } catch (RuntimeException e) {
        return "";
    }
    return "";
}