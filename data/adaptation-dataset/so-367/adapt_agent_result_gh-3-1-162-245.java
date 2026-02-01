void resizeText(int width, int height) {
        CharSequence text = getText();
        if (text == null || text.length() == 0 || height <= 0 || width <= 0 || mTextSize <= 0) {
            return;
        }

        final TextPaint textPaint = getPaint();
        final float oldTextSize = textPaint.getTextSize();

        // Initialize target size to the lesser of default and max
        float targetTextSize = mMaxTextSize > 0 ? Math.min(mTextSize, mMaxTextSize) : mTextSize;
        if (targetTextSize <= 0) {
            return;
        }

        // Decrement loop configuration
        final float stepSize = 1.0f; // px decrement
        int safetyCounter = 0;
        final int maxIterations = 1000;

        // Reduce text size until height fits or minimum size is reached
        int textHeight = getTextHeight(text, textPaint, width, targetTextSize);
        while (textHeight > height && targetTextSize > mMinTextSize && safetyCounter < maxIterations) {
            targetTextSize -= stepSize;
            if (targetTextSize < mMinTextSize) {
                targetTextSize = mMinTextSize;
            }
            textHeight = getTextHeight(text, textPaint, width, targetTextSize);
            safetyCounter++;
        }

        // Ellipsis fallback when minimum size still does not fit
        if (mAddEllipsis && targetTextSize == mMinTextSize && textHeight > height) {
            // Use existing TextPaint directly
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
                    while (end > start && width < lineWidth + ellipsisWidth) {
                        end--;
                        lineWidth = textPaint.measureText(text.subSequence(start, end).toString());
                    }
                    setText(text.subSequence(0, end) + mEllipsis);
                }
            }
        }

        // Apply final size directly to TextPaint
        textPaint.setTextSize(targetTextSize);
        setLineSpacing(mSpacingAdd, mSpacingMult);

        if (mTextResizeListener != null) {
            mTextResizeListener.onTextResize(this, oldTextSize, targetTextSize);
        }

        mNeedsResize = false;
    }