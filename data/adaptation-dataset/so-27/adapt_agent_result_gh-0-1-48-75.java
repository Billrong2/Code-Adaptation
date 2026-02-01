    /**
     * Singleton constructor to get access to the application context and i18n strings.
     *
     * @param context application {@link android.content.Context}
     * @return {@link DateTimeUtils} singleton instance
     */
    public static DateTimeUtils getInstance(Context context) {
        // Preserve existing singleton behavior; avoid NPE on null context
        if (context != null) {
            sCtx = context;
        }
        if (sInstance == null) {
            sInstance = new DateTimeUtils();
            if (sCtx != null) {
                sTimestampLabelYesterday = sCtx.getResources().getString(R.string.WidgetProvider_timestamp_yesterday);
                sTimestampLabelToday = sCtx.getResources().getString(R.string.WidgetProvider_timestamp_today);
                sTimestampLabelJustNow = sCtx.getResources().getString(R.string.WidgetProvider_timestamp_just_now);
                sTimestampLabelMinutesAgo = sCtx.getResources().getString(R.string.WidgetProvider_timestamp_minutes_ago);
                sTimestampLabelHoursAgo = sCtx.getResources().getString(R.string.WidgetProvider_timestamp_hours_ago);
                sTimestampLabelHourAgo = sCtx.getResources().getString(R.string.WidgetProvider_timestamp_hour_ago);
            }
        }
        return sInstance;
    }