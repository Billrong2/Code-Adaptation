public static Integer versionCompareNumerically(final String str1, final String str2) {
    // Defensive handling: treat null or empty versions as equal
    if (str1 == null || str2 == null || str1.length() == 0 || str2.length() == 0) {
        return Integer.valueOf(0);
    }
    try {
        final String[] vals1 = str1.split("\\.");
        final String[] vals2 = str2.split("\\.");
        int index = 0;
        // set index to first non-equal ordinal or length of shortest version string
        while (index < vals1.length && index < vals2.length && vals1[index].equals(vals2[index])) {
            index++;
        }
        // compare first non-equal ordinal number
        if (index < vals1.length && index < vals2.length) {
            int diff = Integer.valueOf(vals1[index]).compareTo(Integer.valueOf(vals2[index]));
            return Integer.valueOf(Integer.signum(diff));
        }
        // the strings are equal or one string is a substring of the other
        // e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
        return Integer.valueOf(Integer.signum(vals1.length - vals2.length));
    } catch (NumberFormatException ignored) {
        // Unparsable version segments are treated as equal
        return Integer.valueOf(0);
    }
}