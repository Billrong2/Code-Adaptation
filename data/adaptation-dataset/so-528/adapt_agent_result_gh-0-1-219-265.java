public Drawable generateBitmap(Drawable input) {
    // If no background images are available, return original drawable unchanged
    if (input == null || backImages == null || backImages.isEmpty()) {
        return input;
    }

    try {
        // Pick a random background safely
        final Random random = new Random();
        final int index = random.nextInt(backImages.size());
        final Bitmap background = backImages.get(index);
        if (background == null) {
            return input;
        }

        final int canvasWidth = background.getWidth();
        final int canvasHeight = background.getHeight();

        // Create output bitmap
        Bitmap output = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        // Draw background
        canvas.drawBitmap(background, 0, 0, paint);

        // Convert input drawable to bitmap if needed
        Bitmap iconBitmap;
        if (input instanceof BitmapDrawable) {
            iconBitmap = ((BitmapDrawable) input).getBitmap();
        } else {
            Bitmap tmp = Bitmap.createBitmap(input.getIntrinsicWidth(), input.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas tmpCanvas = new Canvas(tmp);
            input.setBounds(0, 0, tmpCanvas.getWidth(), tmpCanvas.getHeight());
            input.draw(tmpCanvas);
            iconBitmap = tmp;
        }

        if (iconBitmap != null) {
            // Scale icon according to factor and background size
            final float scale = factor > 0 ? factor : 1.0f;
            int targetWidth = (int) (canvasWidth * scale);
            int targetHeight = (int) (canvasHeight * scale);

            Bitmap scaledIcon = Bitmap.createScaledBitmap(iconBitmap, targetWidth, targetHeight, true);

            int left = (canvasWidth - targetWidth) / 2;
            int top = (canvasHeight - targetHeight) / 2;

            // Draw scaled icon
            canvas.drawBitmap(scaledIcon, left, top, paint);

            // Apply mask if present
            if (maskImage != null) {
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
                canvas.drawBitmap(maskImage, 0, 0, paint);
                paint.setXfermode(null);
            }
        }

        // Overlay front image if present
        if (frontImage != null) {
            canvas.drawBitmap(frontImage, 0, 0, paint);
        }

        // Return composed drawable using icon pack resources
        return new BitmapDrawable(iconPackres, output);

    } catch (OutOfMemoryError oom) {
        Log.e(TAG, "OutOfMemoryError while generating icon bitmap", oom);
        return input;
    } catch (Exception e) {
        Log.e(TAG, "Error while generating icon bitmap", e);
        return input;
    }
}