public static String bytesToHexString(final byte[] bytes) {
    if (bytes == null || bytes.length == 0) {
      return "";
    }
    final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
    final char[] hexChars = new char[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++) {
      final int v = bytes[j] & 0xFF;
      final int baseIndex = j * 2;
      hexChars[baseIndex] = hexArray[v >>> 4];
      hexChars[baseIndex + 1] = hexArray[v & 0x0F];
    }
    return new String(hexChars);
  }