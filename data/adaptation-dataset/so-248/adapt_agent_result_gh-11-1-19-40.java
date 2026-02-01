public static Bitmap getCroppedBitmap(final Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) {
            return null;
        }

        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();

        final Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        paint.setAntiAlias(true);

        final Rect fullRect = new Rect(0, 0, width, height);

        final float centerX = width / 2f;
        final float centerY = height / 2f;
        final float radius = width / 2f;

        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(centerX, centerY, radius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, fullRect, fullRect, paint);

        return output;
    }