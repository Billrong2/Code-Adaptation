static boolean isEqual(InputStream i1, InputStream i2) throws IOException {
    final String TAG = "ExternalProviderTest";
    final int BUFFER_SIZE = 1024; // 1 KB
    byte[] buf1 = new byte[BUFFER_SIZE];
    byte[] buf2 = new byte[BUFFER_SIZE];
    DataInputStream d2 = null;
    long index = 0L;
    try {
      d2 = new DataInputStream(i2);
      int len;
      while ((len = i1.read(buf1)) > 0) {
        d2.readFully(buf2, 0, len);
        for (int i = 0; i < len; i++, index++) {
          if (buf1[i] != buf2[i]) {
            android.util.Log.w(TAG,
              "Byte mismatch at index " + index + ": " + (buf1[i] & 0xFF) + " != " + (buf2[i] & 0xFF));
            return false;
          }
        }
      }
      return d2.read() < 0; // is the end of the second file also.
    }
    catch (EOFException ioe) {
      android.util.Log.w(TAG, "EOFException while comparing streams", ioe);
      return false;
    }
    finally {
      if (i1 != null) {
        try { i1.close(); } catch (IOException ignored) {}
      }
      if (i2 != null) {
        try { i2.close(); } catch (IOException ignored) {}
      }
    }
  }