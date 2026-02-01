    /**
     * Finds the first occurrence of a byte-pattern within the given data using
     * the Knuth–Morris–Pratt (KMP) string-search algorithm adapted for byte arrays.
     * <p>
     * This method scans the {@code data} array for the first index at which the
     * complete {@code pattern} occurs and returns that zero-based index, or
     * {@code -1} if the pattern cannot be found.
     * </p>
     * <p>
     * Implementation is based on a KMP failure-function approach to ensure
     * linear-time complexity relative to the input sizes.
     * </p>
     * <p>
     * Attribution: adapted from an algorithm originally published on Stack Overflow
     * (exact question/answer reference to be filled in).
     * </p>
     *
     * @param data the byte array to search within
     * @param pattern the byte pattern to search for
     * @return the index of the first occurrence of {@code pattern} in {@code data},
     *         or {@code -1} if not found
     */
    private int indexOf(final byte[] data, final byte[] pattern) {
        int[] failure = computeFailure(pattern);

        int j = 0;
        if (data.length == 0) return -1;

        for (int i = 0; i < data.length; i++) {
            while (j > 0 && pattern[j] != data[i]) {
                j = failure[j - 1];
            }
            if (pattern[j] == data[i]) { j++; }
            if (j == pattern.length) {
                return i - pattern.length + 1;
            }
        }
        return -1;
    }