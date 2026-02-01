/**
 * Rotates a bitmap by the specified number of degrees around its center.
 * <p>
 * Based on a Stack Overflow solution for safely rotating bitmaps on Android
 * while handling potential {@link OutOfMemoryError}s.
 * Reference: unknown (original Stack Overflow source not explicitly provided).
 * </p>
 *
 * @param bitmap  The {@link android.graphics.Bitmap} to rotate.
 * @param degrees The number of degrees to rotate the bitmap clockwise.
 * @return The rotated bitmap if rotation succeeds, or the original bitmap
 *         if rotation fails or no rotation is required.
 */
public static Bitmap rotateBitmap(Bitmap bitmap, float degrees) {
    Matrix matrix = new Matrix();
    if (degrees != 0) {
        // Rotate clockwise around the center of the bitmap
        matrix.postRotate(degrees, (float) bitmap.getWidth() / 2,
                (float) bitmap.getHeight() / 2);
    }

    try {
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        if (bitmap != rotatedBitmap) {
            bitmap.recycle();
            bitmap = rotatedBitmap;
        }
    } catch (OutOfMemoryError ex) {
        // Not enough memory to rotate the bitmap; return the original bitmap.
    }
    return bitmap;
}