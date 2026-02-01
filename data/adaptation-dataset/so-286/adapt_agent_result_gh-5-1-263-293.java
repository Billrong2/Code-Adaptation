private static int editDistance(String s1, String s2) {
        // Defensive handling of nulls
        final String lowerS1 = (s1 == null) ? "" : s1.toLowerCase();
        final String lowerS2 = (s2 == null) ? "" : s2.toLowerCase();

        final int length1 = lowerS1.length();
        final int length2 = lowerS2.length();

        // If one of the strings is empty, the distance is the length of the other
        if (length1 == 0) {
            LOGGER.debug("editDistance: s1='" + lowerS1 + "', s2='" + lowerS2 + "', distance=" + length2);
            return length2;
        }
        if (length2 == 0) {
            LOGGER.debug("editDistance: s1='" + lowerS1 + "', s2='" + lowerS2 + "', distance=" + length1);
            return length1;
        }

        final int[] distanceCosts = new int[length2 + 1];

        for (int i = 0; i <= length1; i++) {
            int previousCost = i;
            for (int j = 0; j <= length2; j++) {
                if (i == 0) {
                    distanceCosts[j] = j;
                } else if (j > 0) {
                    int currentCost = distanceCosts[j - 1];
                    if (lowerS1.charAt(i - 1) != lowerS2.charAt(j - 1)) {
                        currentCost = Math.min(Math.min(currentCost, previousCost), distanceCosts[j]) + 1;
                    }
                    distanceCosts[j - 1] = previousCost;
                    previousCost = currentCost;
                }
            }
            distanceCosts[length2] = previousCost;
        }

        final int distance = distanceCosts[length2];
        LOGGER.debug("editDistance: s1='" + lowerS1 + "', s2='" + lowerS2 + "', distance=" + distance);
        return distance;
    }