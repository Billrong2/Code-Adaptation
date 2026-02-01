private Bitmap getCircleBitmap(final Bitmap bitmap) {
    if (bitmap == null || context == null || conferenceUser == null || conferenceUser.getUser() == null) {
        return null;
    }

    final int width = bitmap.getWidth();
    final int height = bitmap.getHeight();
    if (width <= 0 || height <= 0) {
        return null;
    }

    // Use the smallest dimension to ensure a centered circular crop
    final int size = Math.min(width, height);
    final float radiusPx = size / 2f;

    final Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
    final Canvas canvas = new Canvas(output);

    // Convert fixed border width from dp to px using available Context
    final int BORDER_DP = 6;
    final float borderSizePx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            BORDER_DP,
            context.getResources().getDisplayMetrics()
    );

    final Paint paint = new Paint();
    paint.setAntiAlias(true);

    // Clear canvas
    canvas.drawARGB(0, 0, 0, 0);

    // Draw circular mask
    paint.setColor(0xFFFFFFFF);
    paint.setStyle(Paint.Style.FILL);
    canvas.drawCircle(radiusPx, radiusPx, radiusPx, paint);

    // Prepare source and destination rects for centered square crop
    final int left = (width - size) / 2;
    final int top = (height - size) / 2;
    final Rect srcRect = new Rect(left, top, left + size, top + size);
    final Rect dstRect = new Rect(0, 0, size, size);

    // Draw bitmap with SRC_IN compositing
    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    canvas.drawBitmap(bitmap, srcRect, dstRect, paint);
    paint.setXfermode(null);

    // Draw circular border
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeWidth(borderSizePx);
    paint.setColor(conferenceUser.getUser().userColor);
    final float borderRadius = radiusPx - borderSizePx / 2f;
    if (borderRadius > 0) {
        canvas.drawCircle(radiusPx, radiusPx, borderRadius, paint);
    }

    return output;
}