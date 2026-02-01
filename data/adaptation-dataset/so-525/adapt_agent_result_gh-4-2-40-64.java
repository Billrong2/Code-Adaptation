private void refitText(String text, int textWidth) {
    // Constants
    final float MIN_TEXT_SIZE_PX = 10f;
    final float SIZE_THRESHOLD_PX = 0.5f;

    if (text == null || text.length() == 0) return;
    if (textWidth <= 0) return;
    if (mTestPaint == null || getPaint() == null) return;

    int targetWidth = textWidth - getPaddingLeft() - getPaddingRight();
    if (targetWidth <= 0) return;

    // Prepare test paint
    mTestPaint.set(getPaint());

    float upperBound = maxTextSize > 0 ? maxTextSize : getTextSize();
    float lowerBound = MIN_TEXT_SIZE_PX;

    // Clamp bounds
    if (upperBound < lowerBound) upperBound = lowerBound;

    // Binary search for best size
    while ((upperBound - lowerBound) > SIZE_THRESHOLD_PX) {
        float candidateSize = (upperBound + lowerBound) / 2f;
        if (candidateSize <= 0) {
            lowerBound = MIN_TEXT_SIZE_PX;
            break;
        }
        mTestPaint.setTextSize(candidateSize);
        float measuredWidth = mTestPaint.measureText(text);
        if (measuredWidth >= targetWidth) {
            upperBound = candidateSize; // too big
        } else {
            lowerBound = candidateSize; // too small
        }
    }

    float finalSize = Math.max(MIN_TEXT_SIZE_PX, lowerBound);
    // Apply via superclass to bypass overridden setters
    super.setTextSize(TypedValue.COMPLEX_UNIT_PX, finalSize);
}