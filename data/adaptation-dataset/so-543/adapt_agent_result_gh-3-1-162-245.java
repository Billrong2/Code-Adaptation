void resizeText(int width, int height) {
        CharSequence text = getText();
        if (text == null || text.length() == 0 || height <= 0 || width <= 0 || mTextSize <= 0) {
            return;
        }

        final float STEP_SIZE_PX = 2f;

        TextPaint textPaint = getPaint();
        float oldTextSize = textPaint.getTextSize();

        float targetTextSize = mMaxTextSize > 0 ? Math.min(mTextSize, mMaxTextSize) : mTextSize;

        // Measure height only
        int textHeight = getTextHeight(text, textPaint, width, targetTextSize);

        // Decrease text size until it fits height or reaches minimum
        while (textHeight > height && targetTextSize > mMinTextSize) {
            targetTextSize = Math.max(targetTextSize - STEP_SIZE_PX, mMinTextSize);
            textHeight = getTextHeight(text, textPaint, width, targetTextSize);
        }

        // Handle ellipsis when at minimum size and still too tall
        if (mAddEllipsis && targetTextSize == mMinTextSize && textHeight > height) {
            textPaint.setTextSize(targetTextSize);
            StaticLayout layout = new StaticLayout(text, textPaint, width,
                    Alignment.ALIGN_NORMAL, mSpacingMult, mSpacingAdd, false);

            int lineCount = layout.getLineCount();
            if (lineCount > 0) {
                int lastLine = layout.getLineForVertical(height) - 1;
                if (lastLine < 0) {
                    // No line fits at all
                    setText("");
                } else {
                    int start = layout.getLineStart(lastLine);
                    int end = layout.getLineEnd(lastLine);
                    float lineWidth = layout.getLineWidth(lastLine);
                    float ellipsisWidth = textPaint.measureText(mEllipsis);

                    // Trim characters until ellipsis fits
                    while (end > start && lineWidth + ellipsisWidth > width) {
                        end--;
                        lineWidth = textPaint.measureText(text.subSequence(start, end).toString());
                    }
                    setText(text.subSequence(0, end) + mEllipsis);
                }
            } else {
                setText("");
            }
        }

        // Apply final text size and line spacing
        textPaint.setTextSize(targetTextSize);
        setLineSpacing(mSpacingAdd, mSpacingMult);

        if (mTextResizeListener != null) {
            mTextResizeListener.onTextResize(this, oldTextSize, targetTextSize);
        }

        mNeedsResize = false;
    }