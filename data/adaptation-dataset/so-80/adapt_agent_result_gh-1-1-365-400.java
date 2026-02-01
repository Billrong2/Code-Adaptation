@NonNull private static Bitmap scaleCropToFit(@NonNull Bitmap original, int targetWidth, int targetHeight) {
        // Scale the image while preserving aspect ratio, then crop to target size.
        final int originalWidth = original.getWidth();
        final int originalHeight = original.getHeight();

        final float widthScale = (float) targetWidth / (float) originalWidth;
        final float heightScale = (float) targetHeight / (float) originalHeight;

        int startX = 0;
        int startY = 0;
        int scaledWidth;
        int scaledHeight;

        if (widthScale > heightScale) {
            // Scale by width; crop vertically (centered, clamped)
            scaledWidth = targetWidth;
            scaledHeight = Math.round(originalHeight * widthScale);
            int unclampedStartY = (scaledHeight - targetHeight) / 2;
            startY = Math.max(0, Math.min(unclampedStartY, Math.max(0, scaledHeight - targetHeight)));
        } else {
            // Scale by height; crop horizontally from the left edge (no centering)
            scaledHeight = targetHeight;
            scaledWidth = Math.round(originalWidth * heightScale);
            startX = 0;
        }

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(original, scaledWidth, scaledHeight, true);
        Bitmap resizedBitmap = Bitmap.createBitmap(scaledBitmap, startX, startY, targetWidth, targetHeight);

        // Free the intermediate scaled bitmap to reduce memory usage.
        if (!scaledBitmap.isRecycled()) {
            scaledBitmap.recycle();
        }

        return resizedBitmap;
    }