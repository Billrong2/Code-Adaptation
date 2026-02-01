private void refitText(String text, int textWidth) {
        // Only bail out when there is no available width at all
        if (textWidth <= 0) {
            return;
        }

        final int targetWidth = textWidth - getPaddingLeft() - getPaddingRight();
        if (targetWidth <= 0) {
            return;
        }

        // Fixed bounds for binary search (in pixels)
        final float MIN_TEXT_SIZE_PX = 2.0f;
        final float MAX_TEXT_SIZE_PX = 200.0f;
        final float THRESHOLD_PX = 0.5f;

        // Ensure test paint is initialized and cloned from current paint
        if (mTestPaint == null) {
            mTestPaint = new Paint();
        }
        mTestPaint.set(getPaint());

        float lo = MIN_TEXT_SIZE_PX;
        float hi = MAX_TEXT_SIZE_PX;

        // Always run a binary search to find the largest size that fits
        while (hi - lo > THRESHOLD_PX) {
            final float size = (hi + lo) / 2.0f;
            mTestPaint.setTextSize(size);
            final float measuredWidth = mTestPaint.measureText(text);

            if (measuredWidth > targetWidth) {
                // Too large, shrink upper bound
                hi = size;
            } else {
                // Fits (or undershoots), try to grow
                lo = size;
            }
        }

        // Apply undershoot to guarantee the text fits
        setTextSize(TypedValue.COMPLEX_UNIT_PX, lo);
    }