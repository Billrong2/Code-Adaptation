private Bitmap generateBitmap(String componentName, Bitmap defaultBitmap) {
        if(defaultBitmap == null)
            return null;

        if(mBackImages == null || mBackImages.isEmpty())
            return defaultBitmap;

        Bitmap backBitmap = null;
        try {
            long seed = generateSeed(componentName != null ? componentName : "");
            Random random = new Random(seed);
            backBitmap = mBackImages.get(Math.abs(random.nextInt()) % mBackImages.size());
        } catch (RuntimeException e) {
            return defaultBitmap;
        }

        if(backBitmap == null || backBitmap.getWidth() <= 0 || backBitmap.getHeight() <= 0)
            return defaultBitmap;

        Bitmap result;
        try {
            result = Bitmap.createBitmap(backBitmap.getWidth(), backBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        } catch (Throwable t) {
            return defaultBitmap;
        }

        Canvas canvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        // Draw back image
        canvas.drawBitmap(backBitmap, 0, 0, paint);

        // Scale and center default bitmap
        int scaledWidth = (int) (defaultBitmap.getWidth() * mFactor);
        int scaledHeight = (int) (defaultBitmap.getHeight() * mFactor);
        if(scaledWidth <= 0 || scaledHeight <= 0)
            return defaultBitmap;

        Bitmap scaledBitmap;
        try {
            scaledBitmap = Bitmap.createScaledBitmap(defaultBitmap, scaledWidth, scaledHeight, true);
        } catch (Throwable t) {
            return defaultBitmap;
        }

        float left = (backBitmap.getWidth() - scaledWidth) / 2f;
        float top = (backBitmap.getHeight() - scaledHeight) / 2f;

        int saveCount = canvas.saveLayer(0, 0, backBitmap.getWidth(), backBitmap.getHeight(), null);
        canvas.drawBitmap(scaledBitmap, left, top, paint);

        // Apply masking
        if(mMaskImage != null) {
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            canvas.drawBitmap(mMaskImage, 0, 0, paint);
        } else {
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            canvas.drawBitmap(backBitmap, 0, 0, paint);
        }

        paint.setXfermode(null);
        canvas.restoreToCount(saveCount);

        // Draw front overlay if present
        if(mFrontImage != null)
            canvas.drawBitmap(mFrontImage, 0, 0, null);

        return result;
    }