public void resizeText(int availableWidth, int availableHeight) {
    CharSequence text = getText();
    if (text == null || text.length() == 0 || availableWidth <= 0 || availableHeight <= 0) {
        return;
    }

    // Capture old size for resize reporting
    float oldTextSize = mTextSize;

    // Determine starting target size, clamped by min/max
    float targetSize = mTextSize > 0 ? mTextSize : getTextSize();
    if (mMaxTextSize > 0) {
        targetSize = Math.min(targetSize, mMaxTextSize);
    }
    targetSize = Math.max(targetSize, mMinTextSize);

    // Use a cloned TextPaint consistently for measuring and layout
    TextPaint basePaint = getPaint();
    TextPaint measurePaint = new TextPaint(basePaint);

    int textHeight = getTextHeight(text, measurePaint, availableWidth, targetSize);

    // Always shrink text until it fits or we hit the minimum size
    // Use a safe termination condition
    while (textHeight > availableHeight && targetSize > mMinTextSize) {
        targetSize = Math.max(targetSize - 1f, mMinTextSize);
        textHeight = getTextHeight(text, measurePaint, availableWidth, targetSize);
    }

    mTextSize = targetSize;

    // If we reached the minimum size and still don't fit, append an ellipsis
    if (mAddEllipsis && mTextSize == mMinTextSize && textHeight > availableHeight) {
        TextPaint layoutPaint = new TextPaint(basePaint);
        layoutPaint.setTextSize(mTextSize);
        StaticLayout layout = new StaticLayout(text, layoutPaint, availableWidth, Alignment.ALIGN_NORMAL, mSpacingMult, mSpacingAdd, false);
        if (layout.getLineCount() > 0) {
            int lastLine = layout.getLineForVertical(availableHeight) - 1;
            if (lastLine < 0) {
                setText("");
            } else {
                int start = layout.getLineStart(lastLine);
                int end = layout.getLineEnd(lastLine);
                float lineWidth = layout.getLineWidth(lastLine);
                float ellipseWidth = layoutPaint.measureText(mEllipsis);
                while (availableWidth < lineWidth + ellipseWidth && end > start) {
                    end--;
                    lineWidth = layoutPaint.measureText(text.subSequence(start, end).toString());
                }
                setText(text.subSequence(0, end) + mEllipsis);
            }
        }
    }

    // Apply final text size and line spacing
    super.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
    super.setLineSpacing(mSpacingAdd, mSpacingMult);

    // Notify listener if size changed
    if (mTextResizeListener != null && oldTextSize != mTextSize) {
        mTextResizeListener.onTextResize(this, oldTextSize, mTextSize);
    }

    // Clear resize-needed flag
    mNeedsResize = false;
}