public void resizeText(int width, int height) {
        // Use the original, untransformed text for all measurements and resizing
        CharSequence originalText = getText();

        // Do not resize if the view does not have dimensions or there is no text
        if (originalText == null || originalText.length() == 0 || height <= 0 || width <= 0 || mTextSize == 0) {
            return;
        }

        // Get the text view's paint object
        TextPaint textPaint = getPaint();

        // Store the current text size
        float oldTextSize = textPaint.getTextSize();

        // If there is a max text size set, use the lesser of that and the default text size
        float targetTextSize = mMaxTextSize > 0 ? Math.min(mTextSize, mMaxTextSize) : mTextSize;

        // Get the required text height using the original text
        int textHeight = getTextHeight(originalText, textPaint, width, targetTextSize);

        // Until we either fit within our text view or we had reached our min text size, incrementally try smaller sizes
        while (textHeight > height && targetTextSize > mMinTextSize) {
            targetTextSize = Math.max(targetTextSize - 2, mMinTextSize);
            textHeight = getTextHeight(originalText, textPaint, width, targetTextSize);
        }

        // If we had reached our minimum text size and still don't fit, append an ellipsis
        if (mAddEllipsis && targetTextSize == mMinTextSize && textHeight > height) {
            // Draw using a static layout with the original text
            TextPaint paint = new TextPaint(textPaint);
            StaticLayout layout = new StaticLayout(originalText, paint, width, Alignment.ALIGN_NORMAL, mSpacingMult, mSpacingAdd, false);

            // Check that we have at least one line of rendered text
            if (layout.getLineCount() > 0) {
                // Since the line at the specific vertical position would be cut off,
                // we must trim up to the previous line
                int lastLine = layout.getLineForVertical(height) - 1;

                // If the text would not even fit on a single line, clear it
                if (lastLine < 0) {
                    setText("");
                } else {
                    int start = layout.getLineStart(lastLine);
                    int end = layout.getLineEnd(lastLine);
                    float lineWidth = layout.getLineWidth(lastLine);
                    float ellipseWidth = textPaint.measureText(mEllipsis);

                    // Trim characters off until we have enough room to draw the ellipsis
                    while (width < lineWidth + ellipseWidth && end > start) {
                        end--;
                        lineWidth = textPaint.measureText(originalText.subSequence(start, end).toString());
                    }
                    setText(originalText.subSequence(0, end) + mEllipsis);
                }
            }
        }

        // Some devices try to auto adjust line spacing, so force default line spacing
        // and invalidate the layout as a side effect
        setTextSize(TypedValue.COMPLEX_UNIT_PX, targetTextSize);
        setLineSpacing(mSpacingAdd, mSpacingMult);

        // Notify the listener if registered
        if (mTextResizeListener != null) {
            mTextResizeListener.onTextResize(this, oldTextSize, targetTextSize);
        }

        // Reset force resize flag
        mNeedsResize = false;
    }