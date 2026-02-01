private static int versionCompare(final String str1, final String str2) {
    // Adapted from StackOverflow version comparison snippet
    // https://stackoverflow.com/questions/6701948/efficient-way-to-compare-version-strings-in-java

    if (str1 == null || str2 == null) {
        return 0;
    }
    if (str1.isEmpty() || str2.isEmpty()) {
        return 0;
    }

    final String[] vals1 = str1.split("\\.");
    final String[] vals2 = str2.split("\\.");
    int i = 0;

    // set index to first non-equal ordinal or length of shortest version string
    while (i < vals1.length && i < vals2.length && vals1[i].trim().equals(vals2[i].trim())) {
        i++;
    }

    // compare first non-equal ordinal number
    if (i < vals1.length && i < vals2.length) {
        final int diff = Integer.valueOf(vals1[i].trim()).compareTo(Integer.valueOf(vals2[i].trim()));
        return Integer.signum(diff);
    } else {
        // the strings are equal or one string is a substring of the other
        // e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
        return Integer.signum(vals1.length - vals2.length);
    }
}