public static int calculateInSampleSize(final android.graphics.BitmapFactory.Options options, final int reqWidth, final int reqHeight) {
    // Defensive defaults
    if (options == null) {
        return 1;
    }

    final int originalWidth = options.outWidth;
    final int originalHeight = options.outHeight;

    // Validate decoded bounds
    if (originalWidth <= 0 || originalHeight <= 0) {
        return 1;
    }

    // Guard against invalid requested sizes to avoid division by zero
    if (reqWidth <= 0 || reqHeight <= 0) {
        return 1;
    }

    // If no downscaling is needed, keep original behavior
    if (originalWidth <= reqWidth && originalHeight <= reqHeight) {
        return 1;
    }

    // Direct ratio-based calculation (allows non–power-of-two values)
    final float widthRatioFloat = (float) originalWidth / (float) reqWidth;
    final float heightRatioFloat = (float) originalHeight / (float) reqHeight;

    // Round to nearest integer ratios
    final int widthRatio = Math.max(1, Math.round(widthRatioFloat));
    final int heightRatio = Math.max(1, Math.round(heightRatioFloat));

    // Use the smaller ratio to ensure both dimensions meet or exceed requested size
    final int inSampleSize = Math.min(widthRatio, heightRatio);

    return inSampleSize > 0 ? inSampleSize : 1;
}