void resizeText(int width, int height) {
        // Do not resize if not needed or if dimensions/text are invalid
        if (!mNeedsResize) {
            return;
        }
        CharSequence text = getText();
        if (text == null || text.length() == 0 || height <= 0 || width <= 0 || mTextSize <= 0) {
            mNeedsResize = false;
            return;
        }

        final float SIZE_DECREMENT_PX = 2f;

        // Use the view's existing TextPaint directly
        TextPaint textPaint = getPaint();

        // Store the current text size
        float oldTextSize = textPaint.getTextSize();

        // Determine starting target size
        float targetTextSize = mMaxTextSize > 0 ? Math.min(mTextSize, mMaxTextSize) : mTextSize;

        // Measure initial height
        int textHeight = getTextHeight(text, textPaint, width, targetTextSize);

        // Reduce text size until it fits or reaches minimum
        while (textHeight > height && targetTextSize > mMinTextSize) {
            targetTextSize = Math.max(targetTextSize - SIZE_DECREMENT_PX, mMinTextSize);
            textHeight = getTextHeight(text, textPaint, width, targetTextSize);
        }

        // Apply ellipsis if needed at minimum size
        if (mAddEllipsis && targetTextSize == mMinTextSize && textHeight > height) {
            // Ensure paint is at the target size for consistent measurement
            textPaint.setTextSize(targetTextSize);
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
                    // Trim characters until ellipsis fits
                    while (end > start && width < lineWidth + ellipsisWidth) {
                        end--;
                        lineWidth = textPaint.measureText(text.subSequence(start, end).toString());
                    }
                    setText(text.subSequence(0, end) + mEllipsis);
                }
            }
        }

        // Apply the computed text size directly to the TextPaint
        textPaint.setTextSize(targetTextSize);
        // Enforce consistent line spacing
        setLineSpacing(mSpacingAdd, mSpacingMult);

        // Notify listener
        if (mTextResizeListener != null) {
            mTextResizeListener.onTextResize(this, oldTextSize, targetTextSize);
        }

        // Reset force resize flag
        mNeedsResize = false;
    }