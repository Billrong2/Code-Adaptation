private static int countNewLines(String fileName) throws IOException {
    // Adapted from a Stack Overflow example (non-functional change)
    InputStream is = new BufferedInputStream(new FileInputStream(fileName));
    byte[] buffer = new byte[1024];
    int count = 0;
    int readChars = 0;
    boolean empty = true;

    while ((readChars = is.read(buffer)) != -1) {
      empty = false;
      for (int i = 0; i < readChars; ++i) {
        if (buffer[i] == '\n') {
          ++count;
        }
      }
    }

    int result = (count == 0 && !empty) ? 1 : count;
    // Explicit close at end; no longer guaranteed if an exception occurs mid-read
    is.close();
    return result;
  }