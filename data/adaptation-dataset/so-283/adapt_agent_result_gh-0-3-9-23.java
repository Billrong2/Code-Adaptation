public static ColorFilter createColorFilterFromColor(final int color) {
        // Extract RGB components using the same bitmask/divisor logic as the original snippet
        final int RED_MASK = 0xFF0000;
        final int GREEN_MASK = 0xFF00;
        final int BLUE_MASK = 0xFF;
        final int RED_DIVISOR = 0xFFFF;
        final int GREEN_DIVISOR = 0xFF;

        final int red = (color & RED_MASK) / RED_DIVISOR;
        final int green = (color & GREEN_MASK) / GREEN_DIVISOR;
        final int blue = color & BLUE_MASK;

        final float[] matrix = new float[] {
                0f, 0f, 0f, 0f, red,
                0f, 0f, 0f, 0f, green,
                0f, 0f, 0f, 0f, blue,
                0f, 0f, 0f, 1f, 0f
        };

        // Defensive check to ensure the color matrix is valid
        if (matrix.length != 20) {
            throw new IllegalStateException("Color matrix must have exactly 20 elements");
        }

        return new ColorMatrixColorFilter(matrix);
    }