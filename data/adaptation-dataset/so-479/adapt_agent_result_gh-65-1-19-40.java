public static Bitmap getCroppedBitmap(final Bitmap bitmap) {
    if (bitmap == null) {
        throw new IllegalArgumentException("Bitmap must not be null");
    }
    final int width = bitmap.getWidth();
    final int height = bitmap.getHeight();
    if (width <= 0 || height <= 0) {
        throw new IllegalArgumentException("Bitmap must have positive width and height");
    }

    final Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    final Canvas canvas = new Canvas(output);

    final int maskColor = 0xff424242;
    final Paint paint = new Paint();
    final Rect rect = new Rect(0, 0, width, height);

    paint.setAntiAlias(true);
    canvas.drawARGB(0, 0, 0, 0);
    paint.setColor(maskColor);
    canvas.drawCircle(width / 2f, height / 2f, width / 2f, paint);
    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    canvas.drawBitmap(bitmap, rect, rect, paint);

    return output;
}