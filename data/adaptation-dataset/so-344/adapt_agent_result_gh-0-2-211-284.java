public void resizeText(int width, int height) {
        final CharSequence text = getText();
        // Do not resize if the view does not have dimensions or there is no text
        if (text == null || text.length() == 0 || height <= 0 || width <= 0 || mTextSize == 0) {
            return;
        }

        // Get the text view's paint object (read-only) and store the current text size
        final TextPaint viewPaint = getPaint();
        final float oldTextSize = viewPaint.getTextSize();

        // If there is a max text size set, use the lesser of that and the default text size
        float targetTextSize = mMaxTextSize > 0 ? Math.min(mTextSize, mMaxTextSize) : mTextSize;

        // Get the required text height
        int textHeight = getTextHeight(text, viewPaint, width, targetTextSize);

        // Decrement step when shrinking text size
        final float TEXT_SIZE_STEP = 2f;

        // Until we either fit within our text view or we had reached our min text size, incrementally try smaller sizes
        while (textHeight > height && targetTextSize > mMinTextSize) {
            targetTextSize = Math.max(targetTextSize - TEXT_SIZE_STEP, mMinTextSize);
            textHeight = getTextHeight(text, viewPaint, width, targetTextSize);
        }

        // If we had reached our minimum text size and still don't fit, append an ellipsis
        if (mAddEllipsis && targetTextSize == mMinTextSize && textHeight > height && width > 0 && height > 0) {
            // Use a single copied TextPaint instance for all measurements to keep consistency
            final TextPaint paint = new TextPaint(viewPaint);
            paint.setTextSize(targetTextSize);

            // Measure using a static layout
            final StaticLayout layout = new StaticLayout(text, paint, width, Layout.Alignment.ALIGN_NORMAL,
                    mSpacingMult, mSpacingAdd, false);

            // Check that we have at least one line of rendered text
            if (layout.getLineCount() > 0) {
                // Since the line at the specific vertical position would be cut off,
                // we must trim up to the previous line
                final int lastLine = layout.getLineForVertical(height) - 1;

                // If the text would not even fit on a single line, clear it
                if (lastLine < 0) {
                    setText("");
                } else {
                    final int start = layout.getLineStart(lastLine);
                    int end = layout.getLineEnd(lastLine);
                    float lineWidth = layout.getLineWidth(lastLine);
                    final float ellipsisWidth = paint.measureText(M_ELLIPSIS);

                    // Trim characters off until we have enough room to draw the ellipsis
                    while (end > start && width < lineWidth + ellipsisWidth) {
                        end--;
                        lineWidth = paint.measureText(text.subSequence(start, end).toString());
                    }

                    // Guard against underflow
                    if (end > 0) {
                        setText(text.subSequence(0, end) + M_ELLIPSIS);
                    } else {
                        setText(M_ELLIPSIS);
                    }
                }
            }
        }

        // Force default line spacing and apply the computed text size
        setTextSize(TypedValue.COMPLEX_UNIT_PX, targetTextSize);
        setLineSpacing(mSpacingAdd, mSpacingMult);

        // Notify the listener if registered
        if (mTextResizeListener != null) {
            mTextResizeListener.onTextResize(this, oldTextSize, targetTextSize);
        }

        // Reset force resize flag
        mNeedsResize = false;
    }