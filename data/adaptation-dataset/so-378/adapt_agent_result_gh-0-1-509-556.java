public static void lockOrientation(android.app.Activity activity) {
    if (activity == null) {
        return;
    }

    android.view.WindowManager windowManager = activity.getWindowManager();
    if (windowManager == null) {
        return;
    }

    android.view.Display display = windowManager.getDefaultDisplay();
    if (display == null) {
        return;
    }

    android.graphics.Point size = new android.graphics.Point();
    display.getSize(size);
    int width = size.x;
    int height = size.y;

    int rotation = display.getRotation();

    switch (rotation) {
        case android.view.Surface.ROTATION_90:
            if (width > height) {
                activity.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
            else {
                activity.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
            }
            break;
        case android.view.Surface.ROTATION_180:
            if (height > width) {
                activity.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
            }
            else {
                activity.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
            }
            break;
        case android.view.Surface.ROTATION_270:
            if (width > height) {
                activity.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
            }
            else {
                activity.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            break;
        default:
            // Fallback for unexpected rotation values
            if (height > width) {
                activity.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            else {
                activity.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
            break;
    }
}