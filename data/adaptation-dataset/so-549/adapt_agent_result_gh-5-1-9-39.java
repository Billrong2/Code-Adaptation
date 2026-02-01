  /**
   * Checks whether the given string represents a valid integer.
   * A valid integer is an optional leading '-' followed by one or more ASCII digits.
   *
   * @param string the string to validate
   * @return true if the string is a valid integer representation; false otherwise
   * @implNote This method performs a single pass over the characters for linear-time performance.
   */
  public static boolean isInteger(String string) {
    if (string == null) {
      return false;
    }

    int length = string.length();
    if (length == 0) {
      return false;
    }

    int i = 0;
    if (string.charAt(0) == '-') {
      if (length == 1) {
        return false;
      }
      i = 1;
    }

    for (; i < length; i++) {
      char c = string.charAt(i);
      if (c <= '/' || c >= ':') {
        return false;
      }
    }

    return true;
  }