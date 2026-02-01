public static @NotNull Integer versionCompare(@Nullable String str1, @Nullable String str2) {
    // Null-safe and whitespace-tolerant comparison of dotted numeric version strings
    if (str1 == null && str2 == null) {
      return Integer.valueOf(0);
    }
    if (str1 == null) {
      return Integer.valueOf(-1);
    }
    if (str2 == null) {
      return Integer.valueOf(1);
    }

    final String left = str1.trim();
    final String right = str2.trim();

    final String[] parts1 = left.split("\\.");
    final String[] parts2 = right.split("\\.");

    int index = 0;
    // set index to first non-equal ordinal or length of shortest version string
    while (index < parts1.length && index < parts2.length && parts1[index].equals(parts2[index])) {
      index++;
    }

    // compare first non-equal ordinal number
    if (index < parts1.length && index < parts2.length) {
      try {
        // optional numeric validation before parsing
        if (!parts1[index].matches("\\d+") || !parts2[index].matches("\\d+")) {
          throw new NumberFormatException("Non-numeric version segment");
        }
        int diff = Integer.valueOf(parts1[index]).compareTo(Integer.valueOf(parts2[index]));
        return Integer.valueOf(Integer.signum(diff));
      }
      catch (NumberFormatException e) {
        // propagate with clearer context
        throw new IllegalArgumentException("Invalid version format: '" + left + "' vs '" + right + "'", e);
      }
    }
    else {
      // the strings are equal or one string is a substring of the other
      // e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
      return Integer.valueOf(Integer.signum(parts1.length - parts2.length));
    }
  }