private Bitmap generateBitmap(String componentName, Bitmap defaultBitmap) {
        // If no backgrounds or default bitmap, return original
        if(defaultBitmap == null || mBackImages == null || mBackImages.isEmpty()) {
            return defaultBitmap;
        }

        Bitmap backgroundBitmap = null;
        Bitmap resultBitmap = null;
        Canvas canvas = null;

        try {
            // Deterministically pick a background based on component name
            long seed = componentName != null ? generateSeed(componentName) : 0L;
            Random random = new Random(seed);
            backgroundBitmap = mBackImages.get(Math.abs(random.nextInt()) % mBackImages.size());

            if(backgroundBitmap == null
                    || backgroundBitmap.getWidth() <= 0
                    || backgroundBitmap.getHeight() <= 0) {
                return defaultBitmap;
            }

            int bgWidth = backgroundBitmap.getWidth();
            int bgHeight = backgroundBitmap.getHeight();

            resultBitmap = Bitmap.createBitmap(bgWidth, bgHeight, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(resultBitmap);

            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

            // Draw background first
            canvas.drawBitmap(backgroundBitmap, 0, 0, paint);

            // Scale and center the default icon
            float scaleFactor = mFactor > 0f ? mFactor : 1.0f;
            int scaledWidth = Math.round(bgWidth * scaleFactor);
            int scaledHeight = Math.round(bgHeight * scaleFactor);

            if(scaledWidth <= 0 || scaledHeight <= 0) {
                return defaultBitmap;
            }

            Bitmap scaledIcon = Bitmap.createScaledBitmap(defaultBitmap, scaledWidth, scaledHeight, true);
            float left = (bgWidth - scaledWidth) / 2f;
            float top = (bgHeight - scaledHeight) / 2f;

            int saveLayer = canvas.saveLayer(0, 0, bgWidth, bgHeight, null);

            // Draw the icon
            canvas.drawBitmap(scaledIcon, left, top, paint);

            // Apply masking
            if(mMaskImage != null) {
                // Cut out masked regions using DST_OUT
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
                canvas.drawBitmap(mMaskImage, 0, 0, paint);
            } else {
                // Constrain icon to background shape using DST_IN
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
                canvas.drawBitmap(backgroundBitmap, 0, 0, paint);
            }

            paint.setXfermode(null);
            canvas.restoreToCount(saveLayer);

            // Draw optional front overlay
            if(mFrontImage != null) {
                canvas.drawBitmap(mFrontImage, 0, 0, paint);
            }

            return resultBitmap;
        } catch (IllegalArgumentException | RuntimeException e) {
            // Gracefully fall back to default icon on any failure
            return defaultBitmap;
        }
    }