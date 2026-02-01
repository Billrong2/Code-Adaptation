public void resizeText(int width, int height) {
        CharSequence text = getText();
        if (text == null || text.length() == 0 || width <= 0 || height <= 0 || mTextSize <= 0) {
            return;
        }

        final float STEP_PX = 2f;
        if (STEP_PX <= 0) {
            return;
        }

        TextPaint textPaint = getPaint();
        float oldTextSize = textPaint.getTextSize();

        float startSize = mTextSize;
        if (mMaxTextSize > 0) {
            startSize = Math.min(startSize, mMaxTextSize);
        }

        float targetTextSize = startSize;
        int textHeight = getTextHeight(text, textPaint, width, targetTextSize);

        while (textHeight > height && targetTextSize > mMinTextSize) {
            targetTextSize -= STEP_PX;
            if (targetTextSize < mMinTextSize) {
                targetTextSize = mMinTextSize;
            }
            textHeight = getTextHeight(text, textPaint, width, targetTextSize);
        }

        if (mAddEllipsis && targetTextSize == mMinTextSize && textHeight > height) {
            TextPaint paintCopy = new TextPaint(textPaint);
            StaticLayout layout = new StaticLayout(text, paintCopy, width, Alignment.ALIGN_NORMAL, mSpacingMult, mSpacingAdd, false);
            if (layout.getLineCount() > 0) {
                int lastLine = layout.getLineForVertical(height) - 1;
                if (lastLine < 0) {
                    setText("");
                } else {
                    int start = layout.getLineStart(lastLine);
                    int end = layout.getLineEnd(lastLine);
                    float lineWidth = layout.getLineWidth(lastLine);
                    float ellipseWidth = textPaint.measureText(mEllipsis);
                    while (end > start && width < lineWidth + ellipseWidth) {
                        end--;
                        lineWidth = textPaint.measureText(text.subSequence(start, end).toString());
                    }
                    setText(text.subSequence(0, end) + mEllipsis);
                }
            }
        }

        setTextSize(TypedValue.COMPLEX_UNIT_PX, targetTextSize);
        setLineSpacing(mSpacingAdd, mSpacingMult);

        if (mTextResizeListener != null) {
            mTextResizeListener.onTextResize(this, oldTextSize, targetTextSize);
        }

        mNeedsResize = false;
    }