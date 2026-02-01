public static String millisToLongDHMS(long duration) {
    // Humanized handling for very short durations
    if (duration < 5 * ONE_SECOND) {
        return "a few seconds";
    }
    if (duration < ONE_MINUTE) {
        return "a moment";
    }

    StringBuilder res = new StringBuilder();
    long temp;

    // Days
    temp = duration / ONE_DAY;
    if (temp > 0) {
        duration -= temp * ONE_DAY;
        res.append(temp).append(" day").append(temp > 1 ? "s" : "");
    }

    // Hours
    temp = duration / ONE_HOUR;
    if (temp > 0) {
        if (res.length() > 0) {
            res.append(", ");
        }
        duration -= temp * ONE_HOUR;
        res.append(temp).append(" hour").append(temp > 1 ? "s" : "");
    }

    // Minutes
    temp = duration / ONE_MINUTE;
    if (temp > 0) {
        if (res.length() > 0) {
            res.append(", ");
        }
        res.append(temp).append(" minute").append(temp > 1 ? "s" : "");
    }

    return res.toString();
}