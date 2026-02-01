    /**
     * @return the largest non-zero time unit since this date (days/hours/minutes/seconds)
     */
    public String getTimes() {
        long duration = System.currentTimeMillis() - time;

        // handle negative or very small durations defensively
        if (duration <= ONE_SECOND)
            return "0 second";

        long temp;

        temp = duration / ONE_DAY;
        if (temp > 0)
            return temp == 1 ? "day" : temp + " days";

        temp = duration / ONE_HOUR;
        if (temp > 0)
            return temp == 1 ? "hour" : temp + " hours";

        temp = duration / ONE_MINUTE;
        if (temp > 0)
            return temp == 1 ? "minute" : temp + " minutes";

        temp = duration / ONE_SECOND;
        if (temp > 0)
            return temp == 1 ? "second" : temp + " seconds";

        return "";
    }