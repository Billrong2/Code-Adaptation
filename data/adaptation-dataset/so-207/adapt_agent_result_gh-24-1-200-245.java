public static Camera.Size getOptimalPreviewSize(int w, int h) {
    final double ASPECT_TOLERANCE = 0.1;

    if (ManagerCamera.mCamera == null) {
      return null;
    }

    Camera.Parameters parameters = ManagerCamera.mCamera.getParameters();
    if (parameters == null) {
      return null;
    }

    java.util.List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
    if (sizes == null) {
      return null;
    }

    double targetRatio = (double) h / (double) w;
    Camera.Size optimalSize = null;
    double minDiff = Double.MAX_VALUE;
    int targetHeight = h;

    for (Camera.Size size : sizes) {
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

    if (optimalSize == null) {
      minDiff = Double.MAX_VALUE;
      for (Camera.Size size : sizes) {
        double heightDiff = Math.abs(size.height - targetHeight);
        if (heightDiff < minDiff) {
          optimalSize = size;
          minDiff = heightDiff;
        }
      }
    }

    return optimalSize;
  }