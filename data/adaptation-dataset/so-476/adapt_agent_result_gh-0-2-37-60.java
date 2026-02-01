private static Bitmap circleCrop(BitmapPool pool, Bitmap source) {
        if (source == null) return null;

        final int size = Math.min(source.getWidth(), source.getHeight());
        final int x = (source.getWidth() - size) / 2;
        final int y = (source.getHeight() - size) / 2;

        Bitmap squared = null;
        Bitmap result = null;
        try {
            // Crop the source bitmap to a square
            squared = Bitmap.createBitmap(source, x, y, size, size);

            // Try to obtain a bitmap from the pool if available
            if (pool != null) {
                result = pool.get(size, size, Bitmap.Config.ARGB_8888);
            }
            if (result == null) {
                result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            }

            final Canvas canvas = new Canvas(result);
            final Paint paint = new Paint(
                    Paint.ANTI_ALIAS_FLAG |
                    Paint.FILTER_BITMAP_FLAG |
                    Paint.DITHER_FLAG
            );
            paint.setShader(new BitmapShader(
                    squared,
                    BitmapShader.TileMode.CLAMP,
                    BitmapShader.TileMode.CLAMP
            ));

            final float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);
            return result;
        } finally {
            // Recycle the intermediate bitmap to avoid leaks
            if (squared != null && !squared.isRecycled()) {
                squared.recycle();
            }
        }
    }