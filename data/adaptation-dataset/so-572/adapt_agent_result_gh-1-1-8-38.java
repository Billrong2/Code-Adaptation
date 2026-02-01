public static int daysBetween(final Calendar startDate, final Calendar endDate) {
    if (startDate == null || endDate == null) {
        throw new IllegalArgumentException("startDate and endDate must not be null");
    }

    // Work on clones to avoid mutating caller-provided Calendars
    Calendar startCal = (Calendar) startDate.clone();
    Calendar endCal = (Calendar) endDate.clone();

    int sign = 1;
    if (startCal.after(endCal)) {
        Calendar tmp = startCal;
        startCal = endCal;
        endCal = tmp;
        sign = -1;
    }

    final int MILLIS_PER_DAY = 1000 * 60 * 60 * 24;

    long endInstant = endCal.getTimeInMillis();
    int presumedDays = (int) ((endInstant - startCal.getTimeInMillis()) / MILLIS_PER_DAY);

    Calendar cursor = (Calendar) startCal.clone();
    cursor.add(Calendar.DAY_OF_YEAR, presumedDays);
    long instant = cursor.getTimeInMillis();
    if (instant == endInstant) {
        return sign * presumedDays;
    }

    final int step = instant < endInstant ? 1 : -1;
    do {
        cursor.add(Calendar.DAY_OF_MONTH, step);
        presumedDays += step;
    } while (cursor.getTimeInMillis() != endInstant);

    return sign * presumedDays;
}