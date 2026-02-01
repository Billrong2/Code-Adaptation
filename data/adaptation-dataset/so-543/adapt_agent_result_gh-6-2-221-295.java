public void resizeText(int width, int height) {
        final CharSequence text = getText();
        if (text == null || text.length() == 0 || width <= 0 || height <= 0 || mTextSize <= 0) {
            return;
        }

        final TextPaint basePaint = getPaint();
        if (basePaint == null) {
            return;
        }

        final float oldTextSize = basePaint.getTextSize();
        final float maxStartSize = mMaxTextSize > 0 ? Math.min(mTextSize, mMaxTextSize) : mTextSize;
        final float minSize = Math.max(0f, mMinTextSize);
        final float STEP_PX = 2f;

        float targetTextSize = maxStartSize;

        int textHeight = getTextHeight(text, basePaint, width, targetTextSize);

        while (textHeight > height && targetTextSize > minSize) {
            targetTextSize = Math.max(targetTextSize - STEP_PX, minSize);
            textHeight = getTextHeight(text, basePaint, width, targetTextSize);
            if (targetTextSize == minSize) {
                break;
            }
        }

        if (mAddEllipsis && targetTextSize == minSize && textHeight > height) {
            TextPaint measurePaint = new TextPaint(basePaint);
            measurePaint.setTextSize(targetTextSize);

            android.text.StaticLayout layout = new android.text.StaticLayout(
                    text,
                    measurePaint,
                    width,
                    android.text.Layout.Alignment.ALIGN_NORMAL,
                    mSpacingMult,
                    mSpacingAdd,
                    true
            );

            final int lineCount = layout.getLineCount();
            if (lineCount <= 0) {
                setText("");
            } else {
                int lastLine = layout.getLineForVertical(height) - 1;
                if (lastLine < 0) {
                    setText("");
                } else {
                    lastLine = Math.min(lastLine, lineCount - 1);
                    int start = layout.getLineStart(lastLine);
                    int end = layout.getLineEnd(lastLine);

                    float ellipsisWidth = measurePaint.measureText(mEllipsis);
                    float lineWidth = layout.getLineWidth(lastLine);

                    while (end > start && lineWidth + ellipsisWidth > width) {
                        end--;
                        lineWidth = measurePaint.measureText(text.subSequence(start, end).toString());
                    }

                    if (end > 0) {
                        setText(text.subSequence(0, end).toString() + mEllipsis);
                    } else {
                        setText("");
                    }
                }
            }
        }

        setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, targetTextSize);
        setLineSpacing(mSpacingAdd, mSpacingMult);

        if (mTextResizeListener != null) {
            mTextResizeListener.onTextResize(this, oldTextSize, targetTextSize);
        }

        mNeedsResize = false;
    }