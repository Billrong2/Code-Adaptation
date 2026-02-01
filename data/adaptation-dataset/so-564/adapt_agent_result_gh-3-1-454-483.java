    /**
     * Optimized method for converting a {@link String} into an {@link Integer}.
     * <p>
     * This method performs a fast, character-based parsing of the given string and
     * returns an {@link Optional} containing the parsed integer value if the input
     * represents a valid decimal integer. If the input is {@code null}, empty, or
     * contains any non-numeric characters (except for an optional leading minus
     * sign), {@link Optional#empty()} is returned.
     * <p>
     * The parsing logic, error conditions, and return behavior are intentionally
     * equivalent to the original optimized implementation referenced from
     * Stack Overflow, with no semantic changes.
     *
     * @param str the {@link String} holding an integer value
     * @return an {@link Optional} containing the parsed {@code int} value, or
     *         {@link Optional#empty()} if parsing is not possible
     */
    public static Optional<Integer> intValueOfOptional(String str) {
        int idx = 0;
        int end;
        boolean sign = false;
        char ch;

        if ((str == null)
                || ((end = str.length()) == 0)
                || ((((ch = str.charAt(0)) < '0') || (ch > '9'))
                        && (!(sign = ch == '-')
                                || (++idx == end)
                                || ((ch = str.charAt(idx)) < '0')
                                || (ch > '9')))) {
            return Optional.empty();
        }

        int ival = 0;
        for (;; ival *= 10) {
            ival += '0' - ch;
            if (++idx == end) {
                return Optional.of(sign ? ival : -ival);
            }
            if (((ch = str.charAt(idx)) < '0') || (ch > '9')) {
                return Optional.empty();
            }
        }
    }