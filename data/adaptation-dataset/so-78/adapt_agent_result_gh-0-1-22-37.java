public static String toDuration(long duration) {
        // Guard against invalid duration
        if (duration <= 0) {
            return "Just Now";
        }
        // Validate required collections
        if (times == null || timesString == null || times.isEmpty() || timesString.isEmpty()) {
            return "Just Now";
        }
        int max = Math.min(times.size(), timesString.size());
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < max; i++) {
            Long current = times.get(i);
            if (current == null || current <= 0) {
                continue; // avoid division by zero or invalid units
            }
            long temp = duration / current;
            if (temp > 0) {
                result.append(temp)
                      .append(" ")
                      .append(timesString.get(i))
                      .append(temp > 1 ? "s" : "")
                      .append(" ago");
                break;
            }
        }
        if (result.length() == 0) {
            return "Just Now";
        }
        return result.toString();
    }