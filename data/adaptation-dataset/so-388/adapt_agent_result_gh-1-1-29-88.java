public static PendingIntent getClockPendingIntent(final Context context) {
    if (context == null) {
        return null;
    }

    final PackageManager packageManager = context.getPackageManager();
    final Intent alarmClockIntent = new Intent(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_LAUNCHER)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

    // Ordered by precedence; stop at first match
    final String[][] clockImpls = new String[][]{
            {"Standard Android Clock", "com.android.deskclock", "com.android.deskclock.AlarmClock"},
            {"Google Clock", "com.google.android.deskclock", "com.android.deskclock.DeskClock"},
            {"Alarm Klock", "com.arnaud.metivier.alarmclock", "com.arnaud.metivier.alarmclock.AlarmClock"}
    };

    for (int i = 0; i < clockImpls.length; i++) {
        final String packageName = clockImpls[i][1];
        final String className = clockImpls[i][2];
        try {
            final ComponentName componentName = new ComponentName(packageName, className);
            packageManager.getActivityInfo(componentName, PackageManager.GET_META_DATA);
            alarmClockIntent.setComponent(componentName);
            return PendingIntent.getActivity(context, 0, alarmClockIntent, 0);
        } catch (NameNotFoundException e) {
            // Silently ignore and try next implementation
        }
    }

    return null;
}