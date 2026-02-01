/**
     * Convert a millisecond duration to a human-readable string.
     * Output format uses lowercase units with correct singular/plural forms
     * and comma-separated segments (no trailing comma).
     *
     * Source: adapted from a Stack Overflow community answer.
     *
     * @param millis A duration to convert to a string form
     * @return A string such as "1 day, 2 hours, 3 minutes, 4 seconds"
     */
    public static String getDurationBreakdown(long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        final long days = java.util.concurrent.TimeUnit.MILLISECONDS.toDays(millis);
        millis -= java.util.concurrent.TimeUnit.DAYS.toMillis(days);
        final long hours = java.util.concurrent.TimeUnit.MILLISECONDS.toHours(millis);
        millis -= java.util.concurrent.TimeUnit.HOURS.toMillis(hours);
        final long minutes = java.util.concurrent.TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= java.util.concurrent.TimeUnit.MINUTES.toMillis(minutes);
        final long seconds = java.util.concurrent.TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder(64);

        // days
        sb.append(days).append(' ').append(days == 1 ? "day" : "days");
        if (hours > 0 || minutes > 0 || seconds > 0) sb.append(", ");

        // hours
        sb.append(hours).append(' ').append(hours == 1 ? "hour" : "hours");
        if (minutes > 0 || seconds > 0) sb.append(", ");

        // minutes
        sb.append(minutes).append(' ').append(minutes == 1 ? "minute" : "minutes");
        if (seconds > 0) sb.append(", ");

        // seconds (no trailing comma)
        sb.append(seconds).append(' ').append(seconds == 1 ? "second" : "seconds");

        return sb.toString();
    }