/**
     * Creates a {@link android.graphics.Bitmap} from the current OpenGL backbuffer by reading
     * pixels via {@link javax.microedition.khronos.opengles.GL10#glReadPixels} and converting
     * them into an ARGB_8888 bitmap.
     * <p>
     * Adapted from a Stack Overflow answer discussing how to read pixels from an OpenGL surface
     * into a Bitmap on Android:
     * https://stackoverflow.com/
     * </p>
     *
     * @param x  the x-coordinate of the lower-left corner of the region to read
     * @param y  the y-coordinate of the lower-left corner of the region to read
     * @param w  the width of the region to read
     * @param h  the height of the region to read
     * @param gl the current {@link GL10} instance
     * @return a Bitmap containing the read pixels, or null if a {@link android.opengl.GLException}
     *         occurs while reading from the GL surface
     * @throws OutOfMemoryError if the bitmap buffers cannot be allocated
     */
    private Bitmap createBitmapFromGLSurface(final int x, final int y, final int w, final int h, final GL10 gl)
            throws OutOfMemoryError {
        final int[] bitmapBuffer = new int[w * h];
        final int[] bitmapSource = new int[w * h];
        final java.nio.IntBuffer intBuffer = java.nio.IntBuffer.wrap(bitmapBuffer);
        intBuffer.position(0);

        try {
            gl.glReadPixels(x, y, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, intBuffer);
            int offset1;
            int offset2;
            for (int i = 0; i < h; i++) {
                offset1 = i * w;
                offset2 = (h - i - 1) * w;
                for (int j = 0; j < w; j++) {
                    final int texturePixel = bitmapBuffer[offset1 + j];
                    final int blue = (texturePixel >> 16) & 0xff;
                    final int red = (texturePixel << 16) & 0x00ff0000;
                    final int pixel = (texturePixel & 0xff00ff00) | red | blue;
                    bitmapSource[offset2 + j] = pixel;
                }
            }
        } catch (android.opengl.GLException e) {
            android.util.Log.e(TAG, "GLException while reading pixels from GL surface", e);
            return null;
        }

        return android.graphics.Bitmap.createBitmap(bitmapSource, w, h, android.graphics.Bitmap.Config.ARGB_8888);
    }