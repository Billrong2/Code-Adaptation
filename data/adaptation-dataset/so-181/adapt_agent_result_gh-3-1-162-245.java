private void resizeText(int width, int height) {
        CharSequence text = getText();
        if (text == null || text.length() == 0 || width <= 0 || height <= 0) {
            return;
        }

        TextPaint textPaint = getPaint();
        float originalTextSize = textPaint.getTextSize();

        // Determine starting size, explicitly capped by max text size if set
        float currentTextSize = originalTextSize;
        float startSize = currentTextSize;
        if (mMaxTextSize > 0) {
            startSize = Math.min(currentTextSize, mMaxTextSize);
        }

        // Linear decrement step (in pixels)
        final float step = 1.0f;
        if (step <= 0) {
            return;
        }

        float targetTextSize = startSize;
        int textHeight;

        // Linearly decrease text size until it fits or reaches minimum size
        while (targetTextSize > mMinTextSize) {
            textHeight = getTextHeight(text, textPaint, width, targetTextSize);
            if (textHeight <= height) {
                break;
            }
            targetTextSize -= step;
        }

        // Clamp to minimum text size
        if (targetTextSize < mMinTextSize) {
            targetTextSize = mMinTextSize;
        }

        textHeight = getTextHeight(text, textPaint, width, targetTextSize);

        // Handle ellipsis if enabled and text still does not fit at minimum size
        if (mAddEllipsis && targetTextSize == mMinTextSize && textHeight > height) {
            // Ensure paint is at target size for measurement
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

                    if (end > 0) {
                        setText(text.subSequence(0, end) + mEllipsis);
                    } else {
                        setText("");
                    }
                }
            }
        }

        // Apply final size directly to TextPaint
        textPaint.setTextSize(targetTextSize);

        // Force default line spacing and invalidate layout
        setLineSpacing(mSpacingAdd, mSpacingMult);

        // Notify listener if registered
        if (mTextResizeListener != null) {
            mTextResizeListener.onTextResize(this, originalTextSize, targetTextSize);
        }

        // Mark resize as handled
        mNeedsResize = false;
    }