@Override
protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    if (!changed) {
        return;
    }

    // Basic hardening checks
    if (mCamera == null || mPreviewSize == null || mCameraView == null) {
        return;
    }

    final int parentWidth = right - left;
    final int parentHeight = bottom - top;
    if (parentWidth <= 0 || parentHeight <= 0) {
        return;
    }

    // Determine device rotation
    int rotation = android.view.Surface.ROTATION_0;
    try {
        android.view.WindowManager wm = (android.view.WindowManager) mContext.getSystemService(android.content.Context.WINDOW_SERVICE);
        if (wm != null) {
            android.view.Display display = wm.getDefaultDisplay();
            rotation = display.getRotation();
        }
    } catch (Exception e) {
        android.util.Log.w("CameraPreview", "Failed to get display rotation", e);
    }

    // Derive preview width/height based on rotation
    int previewWidth;
    int previewHeight;
    int displayOrientation;
    switch (rotation) {
        case android.view.Surface.ROTATION_90:
            previewWidth = mPreviewSize.height;
            previewHeight = mPreviewSize.width;
            displayOrientation = 90;
            break;
        case android.view.Surface.ROTATION_180:
            previewWidth = mPreviewSize.width;
            previewHeight = mPreviewSize.height;
            displayOrientation = 180;
            break;
        case android.view.Surface.ROTATION_270:
            previewWidth = mPreviewSize.height;
            previewHeight = mPreviewSize.width;
            displayOrientation = 270;
            break;
        case android.view.Surface.ROTATION_0:
        default:
            previewWidth = mPreviewSize.width;
            previewHeight = mPreviewSize.height;
            displayOrientation = 0;
            break;
    }

    if (previewWidth <= 0 || previewHeight <= 0) {
        return;
    }

    // Apply camera display orientation safely
    try {
        mCamera.setDisplayOrientation(displayOrientation);
    } catch (RuntimeException e) {
        android.util.Log.w("CameraPreview", "Failed to set camera display orientation", e);
    }

    // Scale preview to fit parent width while preserving aspect ratio
    float aspectRatio = (float) previewHeight / (float) previewWidth;
    int scaledHeight = (int) (parentWidth * aspectRatio);
    if (scaledHeight <= 0) {
        return;
    }

    // Bottom-align the preview within the container
    int childLeft = 0;
    int childTop = parentHeight - scaledHeight;
    if (childTop < 0) {
        childTop = 0;
    }
    int childRight = parentWidth;
    int childBottom = childTop + scaledHeight;

    layout(childLeft, childTop, childRight, childBottom);
}
