private static String[] splitStringEvery(final String s, final int interval) {
        // Source: Stack Overflow (ID: unknown)
        if (s == null || interval <= 0) {
            return new String[0];
        }

        int arrayLength = (int) Math.ceil((s.length() / (double) interval));
        String[] result = new String[arrayLength];

        int lastIndex = result.length - 1;
        for (int i = 0, j = 0; i < lastIndex; i++, j += interval) {
            result[i] = s.substring(j, j + interval);
        } // Add the last bit
        int j = lastIndex * interval;
        result[lastIndex] = s.substring(j);

        return result;
    }