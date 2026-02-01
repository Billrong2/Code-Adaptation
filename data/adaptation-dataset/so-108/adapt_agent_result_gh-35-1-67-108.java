@Override
protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    // Desired size is based on content size (diameter) plus padding
    final float rawSize = getSize();
    final int contentSize = Math.max(0, (int) rawSize);

    final int horizontalPadding = Math.max(0, getPaddingLeft() + getPaddingRight());
    final int verticalPadding = Math.max(0, getPaddingTop() + getPaddingBottom());

    final int desiredContentWidth = Math.max(0, contentSize + horizontalPadding);
    final int desiredContentHeight = Math.max(0, contentSize + verticalPadding);

    int widthMode = MeasureSpec.getMode(widthMeasureSpec);
    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
    int heightMode = MeasureSpec.getMode(heightMeasureSpec);
    int heightSize = MeasureSpec.getSize(heightMeasureSpec);

    int width;
    int height;

    // Measure Width (unchanged logic)
    if (widthMode == MeasureSpec.EXACTLY) {
        width = widthSize;
    } else if (widthMode == MeasureSpec.AT_MOST) {
        width = Math.min(desiredContentWidth, widthSize);
    } else {
        width = desiredContentWidth;
    }

    // Measure Height (unchanged logic)
    if (heightMode == MeasureSpec.EXACTLY) {
        height = heightSize;
    } else if (heightMode == MeasureSpec.AT_MOST) {
        height = Math.min(desiredContentHeight, heightSize);
    } else {
        height = desiredContentHeight;
    }

    // Must call this to store the measured dimensions
    setMeasuredDimension(width, height);
}