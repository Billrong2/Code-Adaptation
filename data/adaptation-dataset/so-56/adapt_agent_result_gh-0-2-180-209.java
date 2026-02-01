protected final void lockOrientation() {
    if (this.activity == null) {
        return;
    }

    // Always apply the cached orientation lock; compute it once if unset
    if (this.orientationLock == android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
        android.view.WindowManager windowManager = this.activity.getWindowManager();
        if (windowManager != null) {
            android.view.Display display = windowManager.getDefaultDisplay();
            if (display != null) {
                int rotation = display.getRotation();
                int currentOrientation = this.activity.getResources().getConfiguration().orientation;

                int computedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;

                if (currentOrientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
                    if (rotation == android.view.Surface.ROTATION_0 || rotation == android.view.Surface.ROTATION_90) {
                        computedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    } else {
                        computedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    }
                } else if (currentOrientation == android.content.res.Configuration.ORIENTATION_PORTRAIT) {
                    if (rotation == android.view.Surface.ROTATION_0 || rotation == android.view.Surface.ROTATION_270) {
                        computedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    } else {
                        computedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    }
                }

                // Fallback safety: only store a valid computed orientation
                if (computedOrientation != android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
                    this.orientationLock = computedOrientation;
                }
            }
        }
    }

    // Ensure a persistent orientation lock across invocations
    this.activity.setRequestedOrientation(this.orientationLock);
}