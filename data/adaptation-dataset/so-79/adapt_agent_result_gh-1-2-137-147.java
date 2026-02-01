/**
     * Converts a byte count into a human-readable string using SI (1000) or binary (1024) units.
     * <p>
     * When {@code useSIUnits} is {@code true}, prefixes are k, M, G... based on powers of 1000.
     * When {@code false}, prefixes are Ki, Mi, Gi... based on powers of 1024.
     * The value is formatted with one decimal place using the default {@link java.util.Locale}.
     *
     * @param bytes the number of bytes to convert; negative values are treated as 0
     * @param useSIUnits {@code true} for SI units (1000), {@code false} for binary units (1024)
     * @return a human-readable representation such as "1.5 MB" or "1.5 MiB"
     */
    public static String humanReadableByteCount(final long bytes, final boolean useSIUnits) {
        if (bytes <= 0) {
            return "0 B";
        }

        final int unit = useSIUnits ? 1000 : 1024;
        if (bytes < unit) {
            return bytes + " B";
        }

        final String siPrefixes = "kMGTPE";
        final String binaryPrefixes = "KMGTPE";

        int exp = (int) (Math.log(bytes) / Math.log(unit));
        // Clamp exponent to available prefixes to avoid overflow
        exp = Math.min(exp, siPrefixes.length());

        final char prefixChar = (useSIUnits ? siPrefixes : binaryPrefixes).charAt(exp - 1);
        final String prefix = prefixChar + (useSIUnits ? "" : "i");

        final double value = bytes / Math.pow(unit, exp);
        return String.format(java.util.Locale.getDefault(), "%.1f %sB", value, prefix);
    }