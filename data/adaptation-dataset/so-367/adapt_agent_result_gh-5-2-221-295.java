public void resizeText(int width, int height) {
        CharSequence text = getText();
        if (text == null || text.length() == 0 || height <= 0 || width <= 0) {
            return;
        }

        TextPaint textPaint = getPaint();
        if (textPaint == null) {
            return;
        }

        float oldTextSize = textPaint.getTextSize();

        // Initialize target size to the lesser of current size and max size (if set)
        float targetTextSize = oldTextSize;
        if (mMaxTextSize > 0 && mMaxTextSize < targetTextSize) {
            targetTextSize = mMaxTextSize;
        }

        // Ensure sane bounds
        if (targetTextSize < mMinTextSize) {
            targetTextSize = mMinTextSize;
        }

        // Decrement-by-2 loop: only ensure text height fits
        int textHeight = getTextHeight(text, textPaint, width, targetTextSize);
        while (textHeight > height && targetTextSize > mMinTextSize) {
            float nextSize = targetTextSize - 2f;
            if (nextSize < mMinTextSize) {
                nextSize = mMinTextSize;
            }
            // Stop if no progress can be made
            if (nextSize == targetTextSize) {
                break;
            }
            targetTextSize = nextSize;
            textHeight = getTextHeight(text, textPaint, width, targetTextSize);
        }

        // If at minimum size and still too tall, append ellipsis (height-only trigger)
        if (mAddEllipsis && targetTextSize == mMinTextSize && textHeight > height) {
            // Build layout with a copied paint, but do not change its text size
            TextPaint paintCopy = new TextPaint(textPaint);
            StaticLayout layout = new StaticLayout(text, paintCopy, width,
                    Alignment.ALIGN_NORMAL, mSpacingMult, mSpacingAdd, false);

            if (layout.getLineCount() > 0) {
                int lastLine = layout.getLineForVertical(height) - 1;
                if (lastLine < 0) {
                    setText("");
                } else {
                    int start = layout.getLineStart(lastLine);
                    int end = layout.getLineEnd(lastLine);
                    float lineWidth = layout.getLineWidth(lastLine);
                    // Measure ellipsis and text using the original TextPaint
                    float ellipseWidth = textPaint.measureText(mEllipsis);

                    // Trim characters until ellipsis fits
                    while (end > start && width < lineWidth + ellipseWidth) {
                        end--;
                        lineWidth = textPaint.measureText(text.subSequence(start, end).toString());
                    }
                    setText(text.subSequence(0, end) + mEllipsis);
                }
            }
        }

        // Apply final text size and reset line spacing
        setTextSize(TypedValue.COMPLEX_UNIT_PX, targetTextSize);
        setLineSpacing(mSpacingAdd, mSpacingMult);

        // Notify listener
        if (mTextResizeListener != null) {
            mTextResizeListener.onTextResize(this, oldTextSize, targetTextSize);
        }

        // Reset resize flag
        mNeedsResize = false;
    }