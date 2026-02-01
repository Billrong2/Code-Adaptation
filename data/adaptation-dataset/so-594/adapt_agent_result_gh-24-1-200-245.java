public static Camera.Size getOptimalPreviewSize(int w, int h) {
    // Select the optimal preview size from the currently active camera
    final double ASPECT_TOLERANCE = 0.1;

    if (ManagerCamera.mCamera == null) {
      return null;
    }

    android.hardware.Camera.Parameters parameters = ManagerCamera.mCamera.getParameters();
    if (parameters == null) {
      return null;
    }

    java.util.List<android.hardware.Camera.Size> sizes = parameters.getSupportedPreviewSizes();
    if (sizes == null || sizes.isEmpty()) {
      return null;
    }

    double targetRatio = (double) h / (double) w;
    int targetHeight = h;

    android.hardware.Camera.Size optimalSize = null;
    double minDiff = Double.MAX_VALUE;

    // First pass: try to find a size matching the aspect ratio within tolerance
    for (android.hardware.Camera.Size size : sizes) {
      double ratio = (double) size.width / (double) size.height;
      if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) {
        continue;
      }
      double heightDiff = Math.abs(size.height - targetHeight);
      if (heightDiff < minDiff) {
        optimalSize = size;
        minDiff = heightDiff;
      }
    }

    // Fallback: ignore aspect ratio, choose closest height
    if (optimalSize == null) {
      minDiff = Double.MAX_VALUE;
      for (android.hardware.Camera.Size size : sizes) {
        double heightDiff = Math.abs(size.height - targetHeight);
        if (heightDiff < minDiff) {
          optimalSize = size;
          minDiff = heightDiff;
        }
      }
    }

    return optimalSize;
  }