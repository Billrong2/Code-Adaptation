public static String getTimeDiffString(long timestampMillis) {
        // Defensive checks
        if (sCtx == null
                || sTimestampLabelHourAgo == null
                || sTimestampLabelHoursAgo == null
                || sTimestampLabelMinutesAgo == null
                || sTimestampLabelJustNow == null
                || sTimestampLabelToday == null
                || sTimestampLabelYesterday == null
                || sWeekdays == null) {
            return "";
        }

        final long nowMillis = System.currentTimeMillis();
        final long safeTimestamp = Math.min(timestampMillis, nowMillis);
        final long diffMillis = Math.max(0L, nowMillis - safeTimestamp);

        final long totalMinutes = diffMillis / (60L * 1000L);
        final long hours = totalMinutes / 60L;
        final long minutes = totalMinutes % 60L;

        // 0–11 hours: minutes/hours ago logic
        if (hours <= 11L) {
            if (hours <= 0L) {
                if (minutes > 0L) {
                    return String.format(sTimestampLabelMinutesAgo, minutes);
                }
                return sTimestampLabelJustNow;
            }
            if (hours == 1L) {
                return String.format(sTimestampLabelHourAgo, hours);
            }
            return String.format(sTimestampLabelHoursAgo, hours);
        }

        // Day-based logic
        if (DateTimeUtils.isToday(safeTimestamp)) {
            return sTimestampLabelToday;
        }
        if (DateTimeUtils.isYesterday(safeTimestamp)) {
            return sTimestampLabelYesterday;
        }

        // Within last 6 days: weekday name
        if (diffMillis < 6L * millisInADay) {
            final java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTimeInMillis(safeTimestamp);
            final int dayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK);
            if (dayOfWeek >= 0 && dayOfWeek < sWeekdays.length) {
                return sWeekdays[dayOfWeek];
            }
        }

        // Older: numeric date
        return android.text.format.DateUtils.formatDateTime(
                sCtx,
                safeTimestamp,
                android.text.format.DateUtils.FORMAT_NUMERIC_DATE);
    }