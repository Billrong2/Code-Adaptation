public void resizeText(int width, int height) {
    CharSequence text = getText();
    // Do not resize if there is no space or no text
    if (text == null || text.length() == 0 || width <= 0 || height <= 0) {
        return;
    }

    // Account for padding
    int availableWidth = width - getPaddingLeft() - getPaddingRight();
    int availableHeight = height - getPaddingTop() - getPaddingBottom();
    if (availableWidth <= 0 || availableHeight <= 0) {
        return;
    }

    TextPaint textPaint = getPaint();
    float oldTextSize = textPaint.getTextSize();

    // Starting size: min(current text size, max text size if defined)
    float startSize = mTextSize;
    if (mMaxTextSize > 0 && mMaxTextSize < startSize) {
        startSize = mMaxTextSize;
    }

    float targetTextSize = startSize;
    int textHeight = Integer.MAX_VALUE;

    // Linear decrement loop, step of 2px, guarded by min text size
    while (targetTextSize > mMinTextSize) {
        textHeight = getTextHeight(text, textPaint, availableWidth, targetTextSize);
        if (textHeight <= availableHeight) {
            break;
        }
        float nextSize = targetTextSize - 2f;
        if (nextSize < mMinTextSize) {
            targetTextSize = mMinTextSize;
            break;
        }
        targetTextSize = nextSize;
    }

    // Re-check at minimum size
    textHeight = getTextHeight(text, textPaint, availableWidth, targetTextSize);

    // If at minimum size and still doesn't fit, append ellipsis
    if (mAddEllipsis && targetTextSize == mMinTextSize && textHeight > availableHeight) {
        // Use existing TextPaint and shared canvas
        textPaint.setTextSize(targetTextSize);
        sTextResizeCanvas.drawColor(0, android.graphics.PorterDuff.Mode.CLEAR);
        StaticLayout layout = new StaticLayout(text, textPaint, availableWidth, Alignment.ALIGN_NORMAL, mSpacingMult, mSpacingAdd, false);
        layout.draw(sTextResizeCanvas);

        // Determine last visible line and always trim it
        int lastLine = layout.getLineForVertical(availableHeight) - 1;
        int start = layout.getLineStart(lastLine);
        int end = layout.getLineEnd(lastLine);
        float lineWidth = layout.getLineWidth(lastLine);
        float ellipsisWidth = textPaint.measureText(mEllipsis);

        // Trim characters until ellipsis fits
        while (availableWidth < lineWidth + ellipsisWidth && end > start) {
            end--;
            lineWidth = textPaint.measureText(text.subSequence(start, end).toString());
        }

        setText(text.subSequence(0, end) + mEllipsis);
    }

    // Apply final text size directly to the paint and enforce line spacing
    textPaint.setTextSize(targetTextSize);
    setLineSpacing(mSpacingAdd, mSpacingMult);

    // Notify listener
    if (mTextResizeListener != null) {
        mTextResizeListener.onTextResize(this, oldTextSize, targetTextSize);
    }

    // Reset resize flag
    mNeedsResize = false;
}