private Camera.Size getBestPreviewSize(final int width, final int height) {
    // Reference: StackOverflow discussion on choosing the best camera preview size
    if (mCamera == null) {
        return null;
    }

    Camera.Parameters parameters = mCamera.getParameters();
    if (parameters == null) {
        return null;
    }

    java.util.List<Camera.Size> supportedSizes = parameters.getSupportedPreviewSizes();
    if (supportedSizes == null || supportedSizes.isEmpty()) {
        return null;
    }

    Camera.Size result = null;
    for (Camera.Size size : supportedSizes) {
        if (size != null && size.width <= width && size.height <= height) {
            if (result == null) {
                result = size;
            } else {
                int resultArea = result.width * result.height;
                int newArea = size.width * size.height;
                if (newArea > resultArea) {
                    result = size;
                }
            }
        }
    }
    return result;
  }