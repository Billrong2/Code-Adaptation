private static String[] splitStringEvery(String s, int interval) {
        if (s == null || s.length() == 0 || interval <= 0) {
            return new String[0];
        }

        int arrayLength = (int) Math.ceil(s.length() / (double) interval);
        String[] result = new String[arrayLength];

        int j = 0;
        int lastIndex = result.length - 1;
        for (int i = 0; i < lastIndex; i++) {
            result[i] = s.substring(j, j + interval);
            j += interval;
        }

        // Add the last bit only if there is at least one segment
        if (result.length > 0) {
            result[lastIndex] = s.substring(j);
        }

        return result;
    }