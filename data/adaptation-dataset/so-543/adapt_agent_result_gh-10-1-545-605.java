public void resizeText(final int width, final int height) {
        final CharSequence text = getText();
        // Do not resize if there is no text or no vertical space
        if (text == null || text.length() == 0 || height <= 0) {
            mNeedsResize = false;
            return;
        }

        // Get the text view's paint object
        final TextPaint textPaint = getPaint();
        if (textPaint == null) {
            mNeedsResize = false;
            return;
        }

        // Store the current text size
        final float oldTextSize = textPaint.getTextSize();

        // Determine starting text size (do not early-exit when mTextSize == 0)
        float targetTextSize = mMaxTextSize > 0
                ? Math.min(mTextSize, mMaxTextSize)
                : mTextSize;
        if (targetTextSize <= 0) {
            targetTextSize = Math.max(oldTextSize, mMinTextSize);
        }

        // Measure required text height (height-only driven resizing)
        int textHeight = getTextHeight(text, textPaint, width, targetTextSize);

        // Reduce text size until it fits vertically or reaches minimum size
        while (textHeight > height && targetTextSize > mMinTextSize) {
            targetTextSize = Math.max(targetTextSize - 2, mMinTextSize);
            textHeight = getTextHeight(text, textPaint, width, targetTextSize);
        }

        // If at minimum size and still too tall, optionally append ellipsis
        if (mAddEllipsis && targetTextSize == mMinTextSize && textHeight > height && width > 0) {
            final StaticLayout layout = new StaticLayout(text, textPaint, width,
                    Alignment.ALIGN_NORMAL, mSpacingMult, mSpacingAdd, false);
            layout.draw(sTextResizeCanvas);

            final int lastLine = layout.getLineForVertical(height) - 1;
            if (lastLine >= 0) {
                final int start = layout.getLineStart(lastLine);
                int end = layout.getLineEnd(lastLine);
                float lineWidth = layout.getLineWidth(lastLine);
                final float ellipsisWidth = textPaint.measureText(mEllipsis);

                // Trim characters until the ellipsis fits on the last visible line
                while (end > start && width < lineWidth + ellipsisWidth) {
                    end--;
                    lineWidth = textPaint.measureText(text.subSequence(start, end).toString());
                }
                setText(text.subSequence(0, end) + mEllipsis);
            }
        }

        // Apply final text size and force default line spacing
        textPaint.setTextSize(targetTextSize);
        setLineSpacing(mSpacingAdd, mSpacingMult);

        // Notify listener if registered
        if (mTextResizeListener != null) {
            mTextResizeListener.onTextResize(this, oldTextSize, targetTextSize);
        }

        // Reset force resize flag
        mNeedsResize = false;
    }