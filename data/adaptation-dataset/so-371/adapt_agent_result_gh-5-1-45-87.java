@Override
protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    // Reset per-measure state
    line_height = 0;

    final int widthMode = android.view.View.MeasureSpec.getMode(widthMeasureSpec);
    final int widthSize = android.view.View.MeasureSpec.getSize(widthMeasureSpec);
    final int heightMode = android.view.View.MeasureSpec.getMode(heightMeasureSpec);
    final int heightSize = android.view.View.MeasureSpec.getSize(heightMeasureSpec);

    // Available width for children, guarding against negatives
    int availableWidth = widthSize - getPaddingLeft() - getPaddingRight();
    if (availableWidth < 0) {
        availableWidth = 0;
    }

    final int count = getChildCount();
    int x = getPaddingLeft();
    int y = getPaddingTop();

    final int childWidthMeasureSpec = android.view.View.MeasureSpec.makeMeasureSpec(
            availableWidth, android.view.View.MeasureSpec.AT_MOST);
    final int childHeightMeasureSpec = android.view.View.MeasureSpec.makeMeasureSpec(
            0, android.view.View.MeasureSpec.UNSPECIFIED);

    for (int i = 0; i < count; i++) {
        final android.view.View child = getChildAt(i);
        if (child == null || child.getVisibility() == GONE) {
            continue;
        }

        final android.view.ViewGroup.LayoutParams rawLp = child.getLayoutParams();
        if (!(rawLp instanceof LayoutParams)) {
            continue; // skip children with unexpected LayoutParams
        }
        final LayoutParams lp = (LayoutParams) rawLp;

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);

        final int childw = child.getMeasuredWidth();
        final int childh = child.getMeasuredHeight();

        // Wrap to next line if this child would exceed available width
        if (x + childw > getPaddingLeft() + availableWidth) {
            x = getPaddingLeft();
            y += line_height;
            line_height = 0;
        }

        // Track max height for the current line, including vertical spacing
        line_height = Math.max(line_height, childh + lp.vertical_spacing);

        x += childw + lp.horizontal_spacing;
    }

    int measuredHeight = y + line_height + getPaddingBottom();

    // Respect parent height constraints
    if (heightMode == android.view.View.MeasureSpec.AT_MOST) {
        measuredHeight = Math.min(measuredHeight, heightSize);
    } else if (heightMode == android.view.View.MeasureSpec.EXACTLY) {
        measuredHeight = heightSize;
    }

    // Width is determined by parent; height computed above
    setMeasuredDimension(widthSize, measuredHeight);
}