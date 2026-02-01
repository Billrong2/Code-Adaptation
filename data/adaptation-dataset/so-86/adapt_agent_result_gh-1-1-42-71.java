private static boolean hasSoftKeys(Context context) {
    if (context == null) {
        return true;
    }

    final int sdk = Build.VERSION.SDK_INT;

    if (sdk >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        final Object service = context.getSystemService(Context.WINDOW_SERVICE);
        if (!(service instanceof WindowManager)) {
            return true;
        }

        final WindowManager windowManager = (WindowManager) service;
        final Display display = windowManager.getDefaultDisplay();
        if (display == null) {
            return true;
        }

        final DisplayMetrics realMetrics = new DisplayMetrics();
        display.getRealMetrics(realMetrics);
        final int realHeight = realMetrics.heightPixels;
        final int realWidth = realMetrics.widthPixels;

        final DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        final int displayHeight = metrics.heightPixels;
        final int displayWidth = metrics.widthPixels;

        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    } else if (sdk >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
        final boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
        final boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        return !hasMenuKey && !hasBackKey;
    } else {
        // Pre-ICS devices: assume software keys
        return true;
    }
}