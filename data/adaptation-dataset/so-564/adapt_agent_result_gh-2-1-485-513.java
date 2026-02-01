    /**
     * Optimized method for converting a String into an Integer, returning an Optional.
     * <p>
     * Based on the parsing logic from Stack Overflow:
     * http://stackoverflow.com/questions/1030479/most-efficient-way-of-converting-string-to-integer-in-java
     * <p>
     * Instead of throwing {@link NumberFormatException}, this method returns
     * {@link java.util.Optional#empty()} for any invalid input.
     *
     * @param str the String holding an Integer value
     * @return an Optional containing the parsed int value, or Optional.empty() if parsing fails
     */
    public static java.util.Optional<Integer> intValueOfOptional(final String str) {
        int idx = 0;
        int end;
        boolean sign = false;
        char ch;

        // upfront validation: null or empty
        if ((str == null) || ((end = str.length()) == 0)) {
            return java.util.Optional.empty();
        }

        // validate first character and optional sign handling
        if ((((ch = str.charAt(0)) < '0') || (ch > '9'))
                && (!(sign = (ch == '-')) || (++idx == end)
                || ((ch = str.charAt(idx)) < '0') || (ch > '9'))) {
            return java.util.Optional.empty();
        }

        int ival = 0;
        for (;; ival *= 10) {
            ival += '0' - ch;
            if (++idx == end) {
                return java.util.Optional.of(sign ? ival : -ival);
            }
            if (((ch = str.charAt(idx)) < '0') || (ch > '9')) {
                return java.util.Optional.empty();
            }
        }
    }