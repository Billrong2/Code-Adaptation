private static int editDistance(String s1, String s2) {
        if ((s1 == null) || (s2 == null)) {
            LOGGER.debug("editDistance called with null input: s1=" + s1 + ", s2=" + s2);
            return 0;
        }

        final String left = s1.toLowerCase();
        final String right = s2.toLowerCase();

        final int[] costs = new int[right.length() + 1];
        for (int i = 0; i <= left.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= right.length(); j++) {
                if (i == 0) {
                    costs[j] = j;
                } else if (j > 0) {
                    int newValue = costs[j - 1];
                    if (left.charAt(i - 1) != right.charAt(j - 1)) {
                        newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                    }
                    costs[j - 1] = lastValue;
                    lastValue = newValue;
                }
            }
            if (i > 0) {
                costs[right.length()] = lastValue;
            }
        }

        final int distance = costs[right.length()];
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("editDistance inputs: '" + left + "', '" + right + "' => distance=" + distance);
        }
        return distance;
    }