private void refitText(String text, int textWidth) {
        // Defensive checks
        if (text == null || mTestPaint == null || this.getPaint() == null) {
            return;
        }
        if (textWidth <= 0) {
            return;
        }

        final int paddingLeft = this.getPaddingLeft();
        final int paddingRight = this.getPaddingRight();
        final int targetWidth = textWidth - paddingLeft - paddingRight;
        if (targetWidth <= 0) {
            return;
        }

        // Constants (pixels)
        final float MAX_TEXT_SIZE_PX = 100f;
        final float MIN_TEXT_SIZE_PX = 2f;
        final float SIZE_THRESHOLD_PX = 0.5f; // How close we have to be

        float maxSize = MAX_TEXT_SIZE_PX;
        float minSize = MIN_TEXT_SIZE_PX;

        // Initialize test paint from the view's paint
        mTestPaint.set(this.getPaint());

        // Binary search for best text size
        while ((maxSize - minSize) > SIZE_THRESHOLD_PX) {
            float testSize = (maxSize + minSize) / 2f;
            mTestPaint.setTextSize(testSize);

            if (mTestPaint.measureText(text) >= targetWidth) {
                maxSize = testSize; // too big
            } else {
                minSize = testSize; // too small
            }
        }

        // Use minSize so that we undershoot rather than overshoot
        this.setTextSize(TypedValue.COMPLEX_UNIT_PX, minSize);
    }