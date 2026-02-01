private static String byte2HexFormatted(final byte[] bytes) {
    if (bytes == null || bytes.length == 0) {
        return null;
    }
    final StringBuilder hexBuilder = new StringBuilder(bytes.length * 3);
    for (int i = 0; i < bytes.length; i++) {
        final int unsignedByte = bytes[i] & 0xFF;
        final String hex = Integer.toHexString(unsignedByte).toUpperCase();
        final String twoDigitHex = hex.length() > 2 ? hex.substring(hex.length() - 2) : (hex.length() == 1 ? "0" + hex : hex);
        hexBuilder.append(twoDigitHex);
        if (i < bytes.length - 1) {
            hexBuilder.append(':');
        }
    }
    return hexBuilder.toString();
}