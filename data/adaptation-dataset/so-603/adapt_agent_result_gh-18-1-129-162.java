    /**
     * Compares two version strings numerically using dot-delimited segments.
     * <p>
     * This should be used instead of {@link String#compareTo(String)} for version numbers
     * so that, for example, "1.10" is considered greater than "1.6".
     * </p>
     * <p>
     * If either input is null, empty, or contains only whitespace, it is treated as "0".
     * Comparison proceeds by evaluating each numeric segment in order and returning the
     * result of the first difference found. If all compared segments are equal, the version
     * with more segments is considered greater.
     * </p>
     *
     * @param newer a version string (may be null or blank)
     * @param older a version string (may be null or blank)
     * @return a negative integer if {@code newer} is numerically less than {@code older},
     *         a positive integer if {@code newer} is numerically greater than {@code older},
     *         or zero if they are numerically equal
     */
    public static int compareVersionNumbers(final String newer, final String older) {
        // Normalize inputs: null/empty/whitespace treated as "0"
        String v1 = StringUtils.isSetAfterTrim(newer) ? newer.trim() : "0"; //$NON-NLS-1$
        String v2 = StringUtils.isSetAfterTrim(older) ? older.trim() : "0"; //$NON-NLS-1$

        String[] vals1 = v1.split("\\."); //$NON-NLS-1$
        String[] vals2 = v2.split("\\."); //$NON-NLS-1$

        int i = 0;
        // Set index to first non-equal ordinal or length of shortest version string
        while(i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {
            i++;
        }

        // Compare first non-equal ordinal number
        if(i < vals1.length && i < vals2.length) {
            int n1 = 0;
            int n2 = 0;
            try {
                n1 = Integer.parseInt(vals1[i]);
            }
            catch(NumberFormatException ex) {
                n1 = 0; // Fallback for non-numeric tokens
            }
            try {
                n2 = Integer.parseInt(vals2[i]);
            }
            catch(NumberFormatException ex) {
                n2 = 0; // Fallback for non-numeric tokens
            }
            int diff = Integer.valueOf(n1).compareTo(Integer.valueOf(n2));
            return Integer.signum(diff);
        }

        // The strings are equal or one string is a substring of the other
        return Integer.signum(vals1.length - vals2.length);
    }