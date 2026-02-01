private static boolean hasSoftKeys(@NonNull Context context) {
        boolean hasSoftwareKeys = true;

        final WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager == null) {
            return hasSoftwareKeys;
        }

        final Display display = windowManager.getDefaultDisplay();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            final DisplayMetrics realDisplayMetrics = new DisplayMetrics();
            display.getRealMetrics(realDisplayMetrics);

            final int realHeight = realDisplayMetrics.heightPixels;
            final int realWidth = realDisplayMetrics.widthPixels;

            final DisplayMetrics displayMetrics = new DisplayMetrics();
            display.getMetrics(displayMetrics);

            final int displayHeight = displayMetrics.heightPixels;
            final int displayWidth = displayMetrics.widthPixels;

            hasSoftwareKeys = (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            final boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
            final boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            hasSoftwareKeys = !hasMenuKey && !hasBackKey;
        }

        return hasSoftwareKeys;
    }