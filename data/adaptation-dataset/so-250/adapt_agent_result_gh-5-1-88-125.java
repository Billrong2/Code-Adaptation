@android.annotation.TargetApi(android.os.Build.VERSION_CODES.GINGERBREAD)
	public static String getMACAddress(final String interfaceName) throws java.net.SocketException {
		// NOTE: Legacy file-based/BOM MAC retrieval was removed; network interface approach only.
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.GINGERBREAD) {
			throw new RuntimeException("API level < Gingerbread is not supported for MAC retrieval");
		}

		final java.util.Enumeration<java.net.NetworkInterface> interfaces = java.net.NetworkInterface.getNetworkInterfaces();
		if (interfaces == null) {
			return "";
		}

		while (interfaces.hasMoreElements()) {
			final java.net.NetworkInterface intf = interfaces.nextElement();
			if (intf == null) {
				continue;
			}
			if (interfaceName != null && !interfaceName.equalsIgnoreCase(intf.getName())) {
				continue;
			}

			final byte[] mac = intf.getHardwareAddress();
			if (mac == null || mac.length == 0) {
				return "";
			}

			final StringBuilder sb = new StringBuilder(mac.length * 3);
			for (final byte b : mac) {
				sb.append(String.format("%02X:", b));
			}
			if (sb.length() > 0) {
				sb.setLength(sb.length() - 1); // trim trailing colon
			}
			return sb.toString();
		}
		return "";
	}