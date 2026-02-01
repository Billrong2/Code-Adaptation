int getScreenOrientation() {
    final Context ctx = this.context;
    if (ctx == null) {
      Log.w(TAG, "Context is null. Defaulting to portrait orientation.");
      return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    Activity activity = null;
    if (ctx instanceof Activity) {
      activity = (Activity) ctx;
    } else {
      Log.w(TAG, "Context is not an Activity. Defaulting to portrait orientation.");
      return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    final WindowManager windowManager = activity.getWindowManager();
    if (windowManager == null) {
      Log.w(TAG, "WindowManager is null. Defaulting to portrait orientation.");
      return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    final Display display = windowManager.getDefaultDisplay();
    if (display == null) {
      Log.w(TAG, "Display is null. Defaulting to portrait orientation.");
      return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    final int rotation;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
      rotation = display.getRotation();
    } else {
      // Deprecated on newer APIs but required for legacy compatibility
      rotation = display.getOrientation();
    }

    final DisplayMetrics dm = new DisplayMetrics();
    display.getMetrics(dm);
    final int width = dm.widthPixels;
    final int height = dm.heightPixels;

    int orientation;
    // if the device's natural orientation is portrait:
    if (((rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) && height > width) ||
        ((rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) && width > height)) {
      switch (rotation) {
        case Surface.ROTATION_0:
          orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
          break;
        case Surface.ROTATION_90:
          orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
          break;
        case Surface.ROTATION_180:
          orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
          break;
        case Surface.ROTATION_270:
          orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
          break;
        default:
          Log.e(TAG, "Unknown screen orientation. Defaulting to portrait.");
          orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
          break;
      }
    }
    // if the device's natural orientation is landscape or if the device is square:
    else {
      switch (rotation) {
        case Surface.ROTATION_0:
          orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
          break;
        case Surface.ROTATION_90:
          orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
          break;
        case Surface.ROTATION_180:
          orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
          break;
        case Surface.ROTATION_270:
          orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
          break;
        default:
          Log.e(TAG, "Unknown screen orientation. Defaulting to landscape.");
          orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
          break;
      }
    }

    Log.v(TAG, "Computed screen orientation: " + orientation + ", rotation=" + rotation + ", width=" + width + ", height=" + height);
    return orientation;
  }