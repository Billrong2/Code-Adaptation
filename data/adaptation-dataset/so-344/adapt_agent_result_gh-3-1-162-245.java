void resizeText(int width, int height) {
        CharSequence text = getText();
        // Do not resize if the view does not have dimensions or there is no text
        if (text == null || text.length() == 0 || height <= 0 || width <= 0 || mTextSize == 0) {
            return;
        }

        // Operate strictly on raw text (no TransformationMethod)
        TextPaint textPaint = getPaint();

        // Store the current text size from the existing TextPaint
        float oldTextSize = textPaint.getTextSize();

        // Determine the starting target text size
        float targetTextSize = mMaxTextSize > 0 ? Math.min(mTextSize, mMaxTextSize) : mTextSize;

        // Measure text height at the target size
        int textHeight = getTextHeight(text, textPaint, width, targetTextSize);

        // Reduce text size until it fits or reaches the minimum size
        while (textHeight > height && targetTextSize > mMinTextSize) {
            targetTextSize = Math.max(targetTextSize - 2, mMinTextSize);
            textHeight = getTextHeight(text, textPaint, width, targetTextSize);
        }

        // If at minimum size and still does not fit, append an ellipsis if enabled
        if (mAddEllipsis && targetTextSize == mMinTextSize && textHeight > height) {
            // Use the existing TextPaint for layout
            StaticLayout layout = new StaticLayout(text, textPaint, width,
                    Alignment.ALIGN_NORMAL, mSpacingMult, mSpacingAdd, false);
            if (layout.getLineCount() > 0) {
                int lastLine = layout.getLineForVertical(height) - 1;
                if (lastLine < 0) {
                    setText("");
                } else {
                    int start = layout.getLineStart(lastLine);
                    int end = layout.getLineEnd(lastLine);
                    float lineWidth = layout.getLineWidth(lastLine);
                    float ellipsisWidth = textPaint.measureText(mEllipsis);

                    // Trim characters until there is room for the ellipsis
                    while (end > start && width < lineWidth + ellipsisWidth) {
                        end--;
                        lineWidth = textPaint.measureText(text.subSequence(start, end).toString());
                    }
                    setText(text.subSequence(0, end) + mEllipsis);
                }
            }
        }

        // Apply the final text size directly to the TextPaint
        textPaint.setTextSize(targetTextSize);

        // Force default line spacing and trigger layout invalidation
        setLineSpacing(mSpacingAdd, mSpacingMult);

        // Notify the listener if registered
        if (mTextResizeListener != null) {
            mTextResizeListener.onTextResize(this, oldTextSize, targetTextSize);
        }

        // Reset force resize flag
        mNeedsResize = false;
    }