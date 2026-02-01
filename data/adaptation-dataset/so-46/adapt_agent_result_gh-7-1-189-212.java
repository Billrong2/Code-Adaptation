/**
     * Convert a byte count into a human-readable SI-based string representation.
     * <p>
     * Uses decimal (powers of 1,000) units: B, KB, MB, GB, TB, and formats the
     * numeric value with thousands separators and up to one decimal place.
     * </p>
     *
     * @param value the size in bytes; must be >= 1
     * @return a human-readable string representation (e.g., "1.5 MB")
     * @throws IllegalArgumentException if {@code value < 1}
     */
    public static final String convertToByteStringRepresentation(final long value) {
        if (value < 1) {
            throw new IllegalArgumentException("Invalid file size: " + value);
        }

        final long[] dividers = new long[] {
                1_000_000_000_000L,
                1_000_000_000L,
                1_000_000L,
                1_000L,
                1L
        };
        final String[] units = new String[] { "TB", "GB", "MB", "KB", "B" };

        final java.text.DecimalFormat decimalFormat = new java.text.DecimalFormat("#,##0.#");

        for (int i = 0; i < dividers.length; i++) {
            final long divider = dividers[i];
            if (value >= divider) {
                final double formattedValue = divider > 1 ? (double) value / (double) divider : (double) value;
                return decimalFormat.format(formattedValue) + " " + units[i];
            }
        }

        // Should be unreachable due to validation above
        return decimalFormat.format(value) + " B";
    }