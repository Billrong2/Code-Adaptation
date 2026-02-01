private void refitText(String text, int availableWidth) {
    // Guard against null/empty text
    if (text == null || text.length() == 0) {
        return;
    }

    // Account for left/right padding only
    int targetWidth = availableWidth - getPaddingLeft() - getPaddingRight();
    if (targetWidth <= 0) {
        return;
    }

    // Constants for binary search
    final float MIN_TEXT_SIZE_PX = 2f;
    final float MAX_TEXT_SIZE_PX = 100f;
    final float THRESHOLD_PX = 0.5f;

    float lo = MIN_TEXT_SIZE_PX;
    float hi = MAX_TEXT_SIZE_PX;

    // Ensure paint is in sync with the TextView
    mTestPaint.set(getPaint());

    // Binary search for best text size based on width only
    while ((hi - lo) > THRESHOLD_PX) {
        float size = (hi + lo) / 2f;
        mTestPaint.setTextSize(size);

        float measuredWidth = mTestPaint.measureText(text);
        if (measuredWidth >= targetWidth) {
            hi = size; // too big
        } else {
            lo = size; // too small
        }
    }

    // Use the lower bound to undershoot rather than overshoot
    setTextSize(TypedValue.COMPLEX_UNIT_PX, lo);
}