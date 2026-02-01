    /**
     * Decode an NV21 (YUV420) byte array into an RGBA int array (ARGB_8888).
     * <p>
     * This is a pure utility method without any rendering side effects.
     * The conversion logic preserves the original StackOverflow algorithm and
     * returns one packed int per pixel with alpha set to 0xff.
     *
     * @param nv21Data the input image data in NV21 format
     * @param width    image width in pixels (must be > 0)
     * @param height   image height in pixels (must be > 0)
     * @return an int array of length width*height containing RGBA pixels
     * @throws IllegalArgumentException if input data or dimensions are invalid
     */
    private int[] decodeYuvToRgb(final byte[] nv21Data, final int width, final int height) {
        if (nv21Data == null) {
            throw new IllegalArgumentException("nv21Data must not be null");
        }
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("width and height must be > 0");
        }

        final int frameSize = width * height;
        // NV21 requires width*height Y bytes + width*height/2 interleaved VU bytes
        final int requiredLength = frameSize + (frameSize / 2);
        if (nv21Data.length < requiredLength) {
            throw new IllegalArgumentException("nv21Data is too small for the given width/height");
        }

        final int[] rgba = new int[frameSize];

        for (int i = 0; i < height; i++) {
            final int yRowOffset = i * width;
            final int uvRowOffset = frameSize + (i >> 1) * width;
            for (int j = 0; j < width; j++) {
                final int yIndex = yRowOffset + j;
                final int uvIndex = uvRowOffset + (j & ~1);

                // Defensive clamping of indices
                final int safeYIndex = yIndex < nv21Data.length ? yIndex : (nv21Data.length - 1);
                final int safeUvIndex = uvIndex + 1 < nv21Data.length ? uvIndex : (nv21Data.length - 2);

                int y = nv21Data[safeYIndex] & 0xFF;
                int u = nv21Data[safeUvIndex] & 0xFF;
                int v = nv21Data[safeUvIndex + 1] & 0xFF;

                y = y < 16 ? 16 : y;

                int r = Math.round(1.164f * (y - 16) + 1.596f * (v - 128));
                int g = Math.round(1.164f * (y - 16) - 0.813f * (v - 128) - 0.391f * (u - 128));
                int b = Math.round(1.164f * (y - 16) + 2.018f * (u - 128));

                r = r < 0 ? 0 : (r > 255 ? 255 : r);
                g = g < 0 ? 0 : (g > 255 ? 255 : g);
                b = b < 0 ? 0 : (b > 255 ? 255 : b);

                // Preserve original channel packing and alpha
                rgba[yIndex] = 0xff000000 | (b << 16) | (g << 8) | r;
            }
        }

        return rgba;
    }