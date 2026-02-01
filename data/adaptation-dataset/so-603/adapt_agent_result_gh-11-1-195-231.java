private Integer versionCompare(String str1, String str2) {
    // Adapted from a Stack Overflow snippet for numeric version comparison
    if (str1 == null || str2 == null) {
        return Integer.valueOf(0);
    }
    String s1 = str1.trim();
    String s2 = str2.trim();
    if (s1.isEmpty() || s2.isEmpty()) {
        return Integer.valueOf(0);
    }

    String[] vals1 = s1.split("\\.");
    String[] vals2 = s2.split("\\.");
    int i = 0;
    // set index to first non-equal ordinal or length of shortest version string
    while (i < vals1.length && i < vals2.length && vals1[i].trim().equals(vals2[i].trim())) {
        i++;
    }
    // compare first non-equal ordinal number
    if (i < vals1.length && i < vals2.length) {
        try {
            Integer v1 = Integer.valueOf(vals1[i].trim());
            Integer v2 = Integer.valueOf(vals2[i].trim());
            int diff = v1.compareTo(v2);
            return Integer.valueOf(Integer.signum(diff));
        } catch (NumberFormatException e) {
            // Non-numeric segment encountered; treat as equal
            return Integer.valueOf(0);
        }
    } else {
        // the strings are equal or one string is a substring of the other
        // e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
        return Integer.valueOf(Integer.signum(vals1.length - vals2.length));
    }
}