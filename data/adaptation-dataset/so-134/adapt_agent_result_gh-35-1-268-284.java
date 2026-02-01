public static Bitmap drawableToBitmap(Drawable drawable) {
    if (drawable == null) return null;

    if (drawable instanceof BitmapDrawable) {
        return ((BitmapDrawable) drawable).getBitmap();
    }

    final int width = drawable.getIntrinsicWidth();
    final int height = drawable.getIntrinsicHeight();

    if (width <= 0 || height <= 0) return null;

    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);
    drawable.setBounds(0, 0, width, height);
    drawable.draw(canvas);
    return bitmap;
}