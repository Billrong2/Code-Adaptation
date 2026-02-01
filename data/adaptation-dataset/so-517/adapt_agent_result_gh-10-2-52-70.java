public static String toHexStringWithColons(byte[] bytes) {
    // Converts a byte array to an uppercase, colon-separated hex string (e.g., AA:BB:CC)
    if (bytes == null || bytes.length == 0) {
      return "";
    }

    final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
    final int byteCount = bytes.length;
    final int charsPerByteWithSeparator = 3; // two hex chars plus ':'
    final char[] hexChars = new char[byteCount * charsPerByteWithSeparator - 1];

    for (int j = 0; j < byteCount; j++) {
      int v = bytes[j] & 0xFF;
      int baseIndex = j * charsPerByteWithSeparator;
      hexChars[baseIndex] = hexArray[v >>> 4];
      hexChars[baseIndex + 1] = hexArray[v & 0x0F];
      if (j < byteCount - 1) {
        hexChars[baseIndex + 2] = ':';
      }
    }

    return new String(hexChars);
  }