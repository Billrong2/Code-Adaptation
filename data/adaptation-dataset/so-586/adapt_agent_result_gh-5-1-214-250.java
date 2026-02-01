public int onTestSize(int suggestedSize, RectF availableSpace) {
    mPaint.setTextSize(suggestedSize);
    String text = getText();
    boolean singleLine = getMaxLines() == 1;
    if (singleLine) {
        mTextRect.bottom = mPaint.getFontSpacing();
        mTextRect.right = mPaint.measureText(text);
    } else {
        StaticLayout layout = new StaticLayout(text, mPaint,
                mWidthLimit, Alignment.ALIGN_NORMAL, mSpacingMult,
                mSpacingAdd, true);
        if (getMaxLines() != NO_LINE_LIMIT
                && layout.getLineCount() > getMaxLines()) {
            return 1;
        }
        mTextRect.bottom = layout.getHeight();
        int maxWidth = -1;
        for (int i = 0; i < layout.getLineCount(); i++) {
            int lineWidth = (int) layout.getLineWidth(i);
            if (maxWidth < lineWidth) {
                maxWidth = lineWidth;
            }
        }
        mTextRect.right = maxWidth;
    }

    mTextRect.offsetTo(0, 0);
    if (availableSpace.contains(mTextRect)) {
        return -1;
    } else {
        return 1;
    }
}