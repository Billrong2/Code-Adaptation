/**
         * Calculates the optimal camera preview size based on the supplied width and height.
         * <p>
         * This method preserves the original aspect-ratio matching logic while adapting it to
         * {@link android.hardware.Camera.Size}. It is derived from a Stack Overflow example and
         * lightly hardened for null/empty inputs.
         * </p>
         *
         * @param sizes  list of supported camera preview sizes
         * @param width  target width of the preview surface
         * @param height target height of the preview surface
         * @return the optimal {@link android.hardware.Camera.Size}, or a safe fallback if none match
         *
         * Source / Credit: http://stackoverflow.com/questions/7942378/android-camera-will-not-work-startpreview-fails
         */
        private Camera.Size getOptimalPreviewSize(final List<Camera.Size> sizes, final int width, final int height) {
            if (sizes == null || sizes.isEmpty() || width <= 0 || height <= 0) {
                return null;
            }

            Camera.Size optimalSize = null;

            // Aspect ratio tolerance used by the original algorithm
            final double ASPECT_TOLERANCE = 0.1d;
            final double targetRatio = (double) height / (double) width;

            // Try to find a size match which suits the whole screen minus the menu on the left.
            for (final Camera.Size size : sizes) {
                if (size == null) {
                    continue;
                }
                // Preserve original comparison logic
                if (size.height != width) {
                    continue;
                }
                final double ratio = (double) size.width / (double) size.height;
                if (ratio <= targetRatio + ASPECT_TOLERANCE && ratio >= targetRatio - ASPECT_TOLERANCE) {
                    optimalSize = size;
                }
            }

            // Fallback: if no size matched the aspect ratio criteria, choose the first available size
            if (optimalSize == null) {
                optimalSize = sizes.get(0);
            }

            return optimalSize;
        }