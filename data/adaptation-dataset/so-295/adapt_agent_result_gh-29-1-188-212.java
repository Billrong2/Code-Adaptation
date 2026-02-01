private static int calculateInSampleSize(android.graphics.BitmapFactory.Options options, int reqWidth, int reqHeight) {
    // Guard against invalid input
    if (options == null || options.outWidth <= 0 || options.outHeight <= 0 || reqWidth <= 0 || reqHeight <= 0) {
        android.util.Log.d("calculateInSampleSize", "Invalid options or requested size; defaulting inSampleSize=1");
        return 1;
    }

    final int originalWidth = options.outWidth;
    final int originalHeight = options.outHeight;
    int inSampleSize = 1;

    android.util.Log.d("calculateInSampleSize", "Requested width=" + reqWidth + ", height=" + reqHeight);
    android.util.Log.d("calculateInSampleSize", "Original width=" + originalWidth + ", height=" + originalHeight);

    if (originalHeight > reqHeight || originalWidth > reqWidth) {
        // Initial ratios (preserve min(heightRatio, widthRatio) logic)
        final int heightRatio = Math.round((float) originalHeight / (float) reqHeight);
        final int widthRatio = Math.round((float) originalWidth / (float) reqWidth);
        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        if (inSampleSize < 1) {
            inSampleSize = 1;
        }
    }

    android.util.Log.d("calculateInSampleSize", "Initial inSampleSize=" + inSampleSize);

    // Bias toward rounding up: ensure scaled dimensions do not exceed requested bounds
    int scaledWidth = originalWidth / inSampleSize;
    int scaledHeight = originalHeight / inSampleSize;
    while ((scaledWidth > reqWidth || scaledHeight > reqHeight)) {
        inSampleSize++;
        scaledWidth = originalWidth / inSampleSize;
        scaledHeight = originalHeight / inSampleSize;
    }

    if (inSampleSize < 1) {
        inSampleSize = 1;
    }

    android.util.Log.d("calculateInSampleSize", "Final inSampleSize=" + inSampleSize + ", scaledWidth=" + scaledWidth + ", scaledHeight=" + scaledHeight);
    return inSampleSize;
}