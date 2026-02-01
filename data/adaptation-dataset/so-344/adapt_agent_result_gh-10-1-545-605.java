private void resizeText(int width, int height) {
        CharSequence text = getText();
        // Do not resize if the view does not have dimensions or there is no text
        if (text == null || text.length() == 0 || height <= 0 || width <= 0) {
            return;
        }

        // Use the view's TextPaint directly
        TextPaint textPaint = getPaint();

        // Store the current text size
        float oldTextSize = textPaint.getTextSize();
        // If there is a max text size set, use the lesser of that and the default text size
        float targetTextSize = mMaxTextSize > 0 ? Math.min(mTextSize, mMaxTextSize) : mTextSize;

        // Get the required text height
        int textHeight = getTextHeight(text, textPaint, width, targetTextSize);

        // Until we either fit within our text view or we had reached our min text size, incrementally try smaller sizes
        while (textHeight > height && targetTextSize > mMinTextSize) {
            targetTextSize = Math.max(targetTextSize - 2, mMinTextSize);
            textHeight = getTextHeight(text, textPaint, width, targetTextSize);
        }

        // If we had reached our minimum text size and still don't fit, append an ellipsis
        if (mAddEllipsis && targetTextSize == mMinTextSize && textHeight > height) {
            // Draw using a static layout with the view's paint
            textPaint.setTextSize(targetTextSize);
            StaticLayout layout = new StaticLayout(text, textPaint, width, Alignment.ALIGN_NORMAL, mSpacingMult, mSpacingAdd, false);
            // Draw to the shared off-screen canvas
            layout.draw(sTextResizeCanvas);

            // Since the line at the specific vertical position would be cut off,
            // we must trim up to the previous line
            int lastLine = layout.getLineForVertical(height) - 1;
            int start = layout.getLineStart(lastLine);
            int end = layout.getLineEnd(lastLine);
            float lineWidth = layout.getLineWidth(lastLine);
            float ellipseWidth = textPaint.measureText(mEllipsis);

            // Trim characters off until we have enough room to draw the ellipsis
            while (width < lineWidth + ellipseWidth) {
                lineWidth = textPaint.measureText(text.subSequence(start, --end + 1).toString());
            }
            setText(text.subSequence(0, end) + mEllipsis);
        }

        // Some devices try to auto adjust line spacing, so force default line spacing
        // Apply the chosen size directly on the TextPaint
        textPaint.setTextSize(targetTextSize);
        setLineSpacing(mSpacingAdd, mSpacingMult);

        // Notify the listener if registered
        if (mTextResizeListener != null) {
            mTextResizeListener.onTextResize(this, oldTextSize, targetTextSize);
        }

        // Reset force resize flag
        mNeedsResize = false;
    }