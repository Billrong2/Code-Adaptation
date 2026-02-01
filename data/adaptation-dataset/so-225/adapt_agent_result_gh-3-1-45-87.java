@Override
protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
    final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
    final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
    final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

    final int paddingLeft = getPaddingLeft();
    final int paddingRight = getPaddingRight();
    final int paddingTop = getPaddingTop();
    final int paddingBottom = getPaddingBottom();

    // Compute available content width; fall back safely for UNSPECIFIED
    int contentWidth = widthSize - paddingLeft - paddingRight;
    if (widthMode == MeasureSpec.UNSPECIFIED) {
        contentWidth = Integer.MAX_VALUE;
    }
    if (contentWidth < 0) {
        contentWidth = 0;
    }

    int xPosition = paddingLeft;
    int yPosition = paddingTop;
    int currentLineHeight = 0;

    final int count = getChildCount();
    for (int i = 0; i < count; i++) {
        final View child = getChildAt(i);
        if (child == null || child.getVisibility() == GONE) {
            continue;
        }

        final ViewGroup.LayoutParams rawLp = child.getLayoutParams();
        if (!(rawLp instanceof LayoutParams)) {
            continue;
        }
        final LayoutParams lp = (LayoutParams) rawLp;

        // Measure child against available content width and unspecified height
        final int childWidthSpec = MeasureSpec.makeMeasureSpec(contentWidth, MeasureSpec.AT_MOST);
        final int childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        child.measure(childWidthSpec, childHeightSpec);

        final int childWidth = child.getMeasuredWidth();
        final int childHeight = child.getMeasuredHeight();

        // Wrap to next line if needed
        if (xPosition + childWidth > paddingLeft + contentWidth) {
            xPosition = paddingLeft;
            yPosition += currentLineHeight;
            currentLineHeight = 0;
        }

        currentLineHeight = Math.max(currentLineHeight, childHeight + lp.vertical_spacing);
        xPosition += childWidth + lp.horizontal_spacing;
    }

    // Store for onLayout usage
    this.line_height = currentLineHeight;

    int measuredHeight = yPosition + currentLineHeight + paddingBottom;
    if (measuredHeight < paddingTop + paddingBottom) {
        measuredHeight = paddingTop + paddingBottom;
    }

    // Resolve final measured dimensions respecting MeasureSpec modes
    int measuredWidth;
    if (widthMode == MeasureSpec.EXACTLY) {
        measuredWidth = widthSize;
    } else if (widthMode == MeasureSpec.AT_MOST) {
        measuredWidth = Math.min(widthSize, paddingLeft + contentWidth + paddingRight);
    } else {
        measuredWidth = paddingLeft + contentWidth + paddingRight;
    }

    if (heightMode == MeasureSpec.EXACTLY) {
        measuredHeight = heightSize;
    } else if (heightMode == MeasureSpec.AT_MOST) {
        measuredHeight = Math.min(heightSize, measuredHeight);
    }

    setMeasuredDimension(measuredWidth, measuredHeight);
}