/**
   * Compares two version strings numerically rather than lexicographically.
   * <p>
   * Use this instead of {@link String#compareTo(String)} for version strings such as
   * {@code "1.10"} and {@code "1.6"}.
   * </p>
   * <p>
   * Based on a commonly used Stack Overflow solution:
   * https://stackoverflow.com/questions/6701948/efficient-way-to-compare-version-strings-in-java
   * </p>
   * <p>
   * Note: This method treats {@code "1.10"} and {@code "1.10.0"} as different versions.
   * </p>
   *
   * @param str1 a string of ordinal numbers separated by decimal points
   * @param str2 a string of ordinal numbers separated by decimal points
   * @return a negative integer if {@code str1} is numerically less than {@code str2},
   *         a positive integer if {@code str1} is numerically greater than {@code str2},
   *         or zero if the strings are numerically equal
   */
  public static int compareVersion(final String str1, final String str2) {
    final String[] vals1 = str1.split("\\.");
    final String[] vals2 = str2.split("\\.");
    int i = 0;
    // set index to first non-equal ordinal or length of shortest version string
    while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {
      i++;
    }
    // compare first non-equal ordinal number
    if (i < vals1.length && i < vals2.length) {
      final int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
      return Integer.signum(diff);
    } else {
      // the strings are equal or one string is a substring of the other
      // e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
      return Integer.signum(vals1.length - vals2.length);
    }
  }