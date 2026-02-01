public static String formatDurationTill(final long startTimestamp) {
    // Computes elapsed time since the given start timestamp (now - startTimestamp)
    final long now = System.currentTimeMillis();
    long remainingMillis;
    try {
        remainingMillis = now - startTimestamp;
    } catch (ArithmeticException e) {
        // Overflow is highly unlikely with epoch millis, but fall back safely
        remainingMillis = 0L;
    }

    StringBuilder result = new StringBuilder();

    // Fixed iteration order: hours -> minutes -> seconds
    java.util.concurrent.TimeUnit[] units = new java.util.concurrent.TimeUnit[] {
        java.util.concurrent.TimeUnit.HOURS,
        java.util.concurrent.TimeUnit.MINUTES,
        java.util.concurrent.TimeUnit.SECONDS
    };

    for (java.util.concurrent.TimeUnit unit : units) {
        long value = unit.convert(remainingMillis, java.util.concurrent.TimeUnit.MILLISECONDS);
        if (value != 0) {
            remainingMillis -= unit.toMillis(value);
            result.append(value)
                  .append(" ")
                  .append(unit.name().toLowerCase());
            if (Math.abs(value) < 2) {
                // singular: remove trailing 's'
                result.deleteCharAt(result.length() - 1);
            }
            result.append(", ");
        }
    }

    // If nothing was appended (elapsed < 1 second), return milliseconds fallback
    if (result.length() == 0) {
        return remainingMillis + " milliseconds";
    }

    // Remove trailing ", "
    result.delete(result.length() - 2, result.length());

    // Convert last ", " to " and"
    int idx = result.lastIndexOf(", ");
    if (idx > 0) {
        result.deleteCharAt(idx);
        result.insert(idx, " and");
    }

    return result.toString();
}