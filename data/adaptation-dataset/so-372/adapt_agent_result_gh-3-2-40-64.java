private void refitText(String text, int textWidth) {
    // Constants
    final float MIN_TEXT_SIZE_PX = 10f;
    final float MEASURE_THRESHOLD_PX = 0.5f;

    // Code hardening
    final String safeText = text != null ? text : "";
    int targetWidth = textWidth - getPaddingLeft() - getPaddingRight();
    if (targetWidth <= 0) {
        targetWidth = 1; // clamp to a minimal positive width
    }

    // Prepare paint for measurement
    mTestPaint.set(getPaint());

    // Binary search bounds
    float lowerBound = MIN_TEXT_SIZE_PX;
    float upperBound = maxTextSize;

    // Always run binary search to compute final size
    while (upperBound - lowerBound > MEASURE_THRESHOLD_PX) {
        float testSize = (upperBound + lowerBound) / 2f;
        mTestPaint.setTextSize(testSize);
        if (mTestPaint.measureText(safeText) >= targetWidth) {
            upperBound = testSize; // too big
        } else {
            lowerBound = testSize; // too small
        }
    }

    // Apply undershoot result via superclass to avoid override recursion
    super.setTextSize(TypedValue.COMPLEX_UNIT_PX, lowerBound);
}