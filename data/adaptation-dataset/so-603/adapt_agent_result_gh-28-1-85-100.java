private static Integer versionCompare(String str1, String str2) {
    // Compare two dotted numeric version strings numerically (non-lexicographical).
    String[] vals1 = str1.split("\\.");
    String[] vals2 = str2.split("\\.");
    int i = 0;
    // set index to first non-equal ordinal or length of shortest version string
    while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {
        i++;
    }
    // compare first non-equal ordinal number
    if (i < vals1.length && i < vals2.length) {
        return Integer.signum(Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i])));
    }
    // the strings are equal or one string is a substring of the other
    return Integer.signum(vals1.length - vals2.length);
}