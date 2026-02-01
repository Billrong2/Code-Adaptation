/**
     * Formats a string using dictionary-style placeholders of the form %(key).
     * Each placeholder is converted to a positional format specifier and
     * substituted using the corresponding value from the provided map.
     *
     * @param format the input format string containing %(key) placeholders
     * @param values a map of keys to values used for substitution
     * @return the formatted string with all placeholders resolved
     */
    public static String dictFormat(final String format, final Map<String, Object> values) {
        if (format == null) {
            return null;
        }
        if (values == null || values.isEmpty()) {
            return format;
        }

        final StringBuilder convertedFormat = new StringBuilder(format);
        final ArrayList<Object> arguments = new ArrayList<Object>();
        int currentPos = 1;

        for (Map.Entry<String, Object> entry : values.entrySet()) {
            final String key = entry.getKey();
            final String formatKey = "%(" + key + ")";
            final String formatPos = "%" + currentPos + "$s";

            int index = 0;
            while ((index = convertedFormat.indexOf(formatKey, index)) != -1) {
                convertedFormat.replace(index, index + formatKey.length(), formatPos);
                index += formatPos.length();
            }

            Object value = entry.getValue();
            arguments.add(value != null ? value : "");
            currentPos++;
        }

        // Debug print of the converted format string
        System.out.println("Converted format: " + convertedFormat.toString());

        try {
            return String.format(convertedFormat.toString(), arguments.toArray());
        } catch (java.util.IllegalFormatException e) {
            throw new IllegalArgumentException("Invalid format after conversion: " + convertedFormat, e);
        }
    }