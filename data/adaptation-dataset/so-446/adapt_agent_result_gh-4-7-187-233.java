/**
 * Rotate and/or flip a bitmap based on the given EXIF orientation.
 *
 * @param bitmap The source bitmap to transform.
 * @param orientation One of {@link android.media.ExifInterface} orientation constants.
 * @return A new, transformed bitmap, the original bitmap if no transform is needed,
 *         or {@code null} if the operation fails.
 */
public static Bitmap rotateBitmap(final Bitmap bitmap, final int orientation)
{
    if(bitmap == null)
        return null;

    final Matrix matrix = new Matrix();
    switch(orientation)
    {
        case ExifInterface.ORIENTATION_NORMAL:
            return bitmap;
        case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
            matrix.setScale(-1, 1);
            break;
        case ExifInterface.ORIENTATION_ROTATE_180:
            matrix.setRotate(180);
            break;
        case ExifInterface.ORIENTATION_FLIP_VERTICAL:
            matrix.setRotate(180);
            matrix.postScale(-1, 1);
            break;
        case ExifInterface.ORIENTATION_TRANSPOSE:
            matrix.setRotate(90);
            matrix.postScale(-1, 1);
            break;
        case ExifInterface.ORIENTATION_ROTATE_90:
            matrix.setRotate(90);
            break;
        case ExifInterface.ORIENTATION_TRANSVERSE:
            matrix.setRotate(-90);
            matrix.postScale(-1, 1);
            break;
        case ExifInterface.ORIENTATION_ROTATE_270:
            matrix.setRotate(-90);
            break;
        default:
            return bitmap;
    }

    try
    {
        Bitmap rotated = Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.getWidth(),
                bitmap.getHeight(),
                matrix,
                true
        );

        if(rotated != bitmap)
            bitmap.recycle();

        return rotated;
    }
    catch(OutOfMemoryError e)
    {
        e.printStackTrace();
        return null;
    }
}