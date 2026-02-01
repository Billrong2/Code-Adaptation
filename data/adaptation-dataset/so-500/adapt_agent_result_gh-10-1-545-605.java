public void resizeText(int width, int height) {
        CharSequence text = getText();
        if (text == null || text.length() == 0 || width <= 0 || height <= 0) {
            return;
        }

        // Use the TextView's existing TextPaint directly
        TextPaint textPaint = getPaint();
        float oldTextSize = textPaint.getTextSize();

        // Determine starting target size
        float targetTextSize = mMaxTextSize > 0 ? Math.min(mTextSize, mMaxTextSize) : mTextSize;
        if (targetTextSize <= 0) {
            // Fall back to current paint size if unset
            targetTextSize = oldTextSize;
        }

        // Measure text height and shrink until it fits or reaches minimum size
        int textHeight = getTextHeight(text, textPaint, width, targetTextSize);
        while (textHeight > height && targetTextSize > mMinTextSize) {
            targetTextSize = Math.max(targetTextSize - 2f, mMinTextSize);
            textHeight = getTextHeight(text, textPaint, width, targetTextSize);
        }

        // If still too large at minimum size, append ellipsis
        if (mAddEllipsis && targetTextSize == mMinTextSize && textHeight > height) {
            // Build StaticLayout with the same TextPaint and draw immediately
            StaticLayout layout = new StaticLayout(text, textPaint, width, Alignment.ALIGN_NORMAL, mSpacingMult, mSpacingAdd, false);
            layout.draw(sTextResizeCanvas);

            // Compute last visible line directly and trim
            int lastLine = layout.getLineForVertical(height) - 1;
            int start = layout.getLineStart(lastLine);
            int end = layout.getLineEnd(lastLine);

            float lineWidth = layout.getLineWidth(lastLine);
            float ellipsisWidth = textPaint.measureText(mEllipsis);

            // Trim characters until ellipsis fits
            while (width < lineWidth + ellipsisWidth && end > start) {
                end--;
                lineWidth = textPaint.measureText(text.subSequence(start, end).toString());
            }

            setText(text.subSequence(0, end).toString() + mEllipsis);
        }

        // Apply final text size directly on the TextPaint
        textPaint.setTextSize(targetTextSize);

        // Force default line spacing to invalidate layout
        setLineSpacing(mSpacingAdd, mSpacingMult);

        // Notify listener if registered
        if (mTextResizeListener != null) {
            mTextResizeListener.onTextResize(this, oldTextSize, targetTextSize);
        }

        mNeedsResize = false;
    }