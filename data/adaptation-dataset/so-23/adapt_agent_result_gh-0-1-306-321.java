private void storeGreyscaleMapIntoAlpha(final BufferedImage imageWithoutAlpha, final BufferedImage greyscaleImage) {
        // Packs a greyscale image (using its blue channel) into the alpha channel of a base RGB image.
        if (imageWithoutAlpha == null || greyscaleImage == null) {
            return;
        }
        int width = imageWithoutAlpha.getWidth();
        int height = imageWithoutAlpha.getHeight();
        if (width != greyscaleImage.getWidth() || height != greyscaleImage.getHeight()) {
            return;
        }

        // Assumes INT_ARGB-compatible images; RGB is preserved while alpha is replaced from greyscale blue channel.
        int[] basePixels = imageWithoutAlpha.getRGB(0, 0, width, height, null, 0, width);
        int[] greyscalePixels = greyscaleImage.getRGB(0, 0, width, height, null, 0, width);

        for (int i = 0; i < basePixels.length; i++) {
            int rgb = basePixels[i] & 0x00ffffff; // mask preexisting alpha
            int alpha = greyscalePixels[i] << 24; // shift blue channel into alpha
            basePixels[i] = rgb | alpha;
        }

        imageWithoutAlpha.setRGB(0, 0, width, height, basePixels, 0, width);
    }