public static Bitmap drawableToBitmap(final Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            final BitmapDrawable bd = (BitmapDrawable) drawable;
            // Configure for higher-quality rendering (may mutate drawable state)
            if (bd.getPaint() != null) {
                bd.getPaint().setAntiAlias(true);
                bd.getPaint().setDither(true);
                bd.getPaint().setFilterBitmap(true);
            }
            // Set target density to a high value for better scaling quality
            bd.setTargetDensity(android.util.DisplayMetrics.DENSITY_HIGH);

            final Bitmap existing = bd.getBitmap();
            if (existing != null) {
                return existing;
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            // Single color bitmap will be created of 1x1 pixel
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        final Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }