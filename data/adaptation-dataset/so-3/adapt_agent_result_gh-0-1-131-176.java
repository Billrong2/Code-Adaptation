/**
     * Computes the appropriate screen orientation constant for the given activity
     * without applying any side effects.
     * <p>
     * This preserves the original logic used to determine the device natural
     * orientation and maps the current display rotation to the corresponding
     * {@link android.content.pm.ActivityInfo} constant.
     * <p>
     * Source logic adapted from a Stack Overflow answer about locking screen
     * orientation based on natural device orientation.
     *
     * @param activity the activity used to retrieve configuration and display data
     * @return one of {@link ActivityInfo} SCREEN_ORIENTATION_* constants, or
     * {@link ActivityInfo#SCREEN_ORIENTATION_UNSPECIFIED} as a safe fallback
     */
    public static int getScreenOrientation(Activity activity) {
        if (activity == null) {
            return ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
        }

        WindowManager windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager == null || windowManager.getDefaultDisplay() == null) {
            return ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
        }

        Configuration configuration = activity.getResources() != null
            ? activity.getResources().getConfiguration()
            : null;
        if (configuration == null) {
            return ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
        }

        int rotation = windowManager.getDefaultDisplay().getRotation();

        // Determine the natural orientation of the device
        boolean naturalLandscape =
            (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE &&
                (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180)) ||
            (configuration.orientation == Configuration.ORIENTATION_PORTRAIT &&
                (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270));

        if (naturalLandscape) {
            // Natural position is landscape
            switch (rotation) {
                case Surface.ROTATION_0:
                    return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                case Surface.ROTATION_90:
                    return ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                case Surface.ROTATION_180:
                    return ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                case Surface.ROTATION_270:
                    return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                default:
                    return ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
            }
        }
        else {
            // Natural position is portrait
            switch (rotation) {
                case Surface.ROTATION_0:
                    return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                case Surface.ROTATION_90:
                    return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                case Surface.ROTATION_180:
                    return ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                case Surface.ROTATION_270:
                    return ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                default:
                    return ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
            }
        }
    }