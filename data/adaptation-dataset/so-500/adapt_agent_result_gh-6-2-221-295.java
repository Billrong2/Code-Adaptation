public void resizeText(int width, int height) {
        final CharSequence text = getText();

        // Do not resize if there is no text or no available space
        if (text == null || text.length() == 0 || width <= 0 || height <= 0 || mTextSize <= 0) {
            return;
        }

        // Get the TextView paint (read-only; do not mutate directly)
        final TextPaint textPaint = getPaint();

        // Store the current text size
        final float oldTextSize = textPaint.getTextSize();

        // Determine the starting target size
        float targetTextSize = mMaxTextSize > 0 ? Math.min(mTextSize, mMaxTextSize) : mTextSize;

        // Measure text height with the target size
        int textHeight = getTextHeight(text, textPaint, width, targetTextSize);

        // Decrement step when resizing (px)
        final float decrementStep = 2f;

        // Reduce text size until it fits or reaches the minimum size
        while (textHeight > height && targetTextSize > mMinTextSize) {
            targetTextSize = Math.max(targetTextSize - decrementStep, mMinTextSize);
            textHeight = getTextHeight(text, textPaint, width, targetTextSize);
        }

        // If we reached the minimum size and still do not fit, append an ellipsis
        if (mAddEllipsis && targetTextSize == mMinTextSize && textHeight > height && mEllipsis != null) {
            // Use a copy of TextPaint for measuring
            final TextPaint paintCopy = new TextPaint(textPaint);
            final StaticLayout layout = new StaticLayout(text, paintCopy, width,
                    Alignment.ALIGN_NORMAL, mSpacingMult, mSpacingAdd, false);

            if (layout.getLineCount() > 0) {
                // Trim to the last fully visible line
                final int lastLine = layout.getLineForVertical(height) - 1;

                if (lastLine < 0) {
                    // Not even a single line fits
                    setText("");
                } else {
                    int start = layout.getLineStart(lastLine);
                    int end = layout.getLineEnd(lastLine);
                    float lineWidth = layout.getLineWidth(lastLine);
                    final float ellipsisWidth = textPaint.measureText(mEllipsis);

                    // Trim characters until the ellipsis fits
                    while (end > start && width < lineWidth + ellipsisWidth) {
                        lineWidth = textPaint.measureText(text.subSequence(start, --end + 1).toString());
                    }

                    setText(text.subSequence(0, end) + mEllipsis);
                }
            }
        }

        // Apply the computed text size and restore line spacing
        setTextSize(TypedValue.COMPLEX_UNIT_PX, targetTextSize);
        setLineSpacing(mSpacingAdd, mSpacingMult);

        // Notify listener if registered
        if (mTextResizeListener != null) {
            mTextResizeListener.onTextResize(this, oldTextSize, targetTextSize);
        }

        // Reset resize flag
        mNeedsResize = false;
    }