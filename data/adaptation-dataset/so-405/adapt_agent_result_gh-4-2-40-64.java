private void refitText(final String text, final int textWidth) {
        // Hardening: avoid unnecessary work
        if (text == null || text.length() == 0 || textWidth <= 0) {
            return;
        }

        final int horizontalPadding = getPaddingLeft() + getPaddingRight();
        final int targetWidth = textWidth - horizontalPadding;
        if (targetWidth <= 0) {
            return;
        }

        // Bounds and precision
        final float minSizePx = 10f; // increased lower bound
        final float maxSizePx = maxTextSize > 0 ? maxTextSize : getTextSize();
        final float threshold = 0.5f; // how close we have to be

        float low = minSizePx;
        float high = maxSizePx;

        // Ensure test paint is in sync with the current paint
        mTestPaint.set(getPaint());

        while ((high - low) > threshold) {
            final float size = (high + low) / 2f;
            mTestPaint.setTextSize(size);
            if (mTestPaint.measureText(text) >= targetWidth) {
                high = size; // too big
            } else {
                low = size; // too small
            }
        }

        // Use low so that we undershoot rather than overshoot
        super.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, low);
    }