@Override
protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    final int widthMode = android.view.View.MeasureSpec.getMode(widthMeasureSpec);
    final int heightMode = android.view.View.MeasureSpec.getMode(heightMeasureSpec);

    // Resolve available width after padding
    int availableWidth = android.view.View.MeasureSpec.getSize(widthMeasureSpec)
            - getPaddingLeft() - getPaddingRight();
    if (availableWidth < 0) {
        availableWidth = 0;
    }

    final int count = getChildCount();

    int xPos = getPaddingLeft();
    int yPos = getPaddingTop();

    int currentLineHeight = 0;
    int maxWidthUsed = 0;

    // Children always measure their height as UNSPECIFIED (natural height)
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
            continue;
        }
        final LayoutParams lp = (LayoutParams) rawLp;

        final int hSpacing = Math.max(0, lp.horizontal_spacing);
        final int vSpacing = Math.max(0, lp.vertical_spacing);

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);

        final int childWidth = child.getMeasuredWidth();
        final int childHeight = child.getMeasuredHeight();

        // Line break if needed
        if (xPos + childWidth > getPaddingLeft() + availableWidth) {
            yPos += currentLineHeight;
            xPos = getPaddingLeft();
            currentLineHeight = 0;
        }

        xPos += childWidth + hSpacing;
        maxWidthUsed = Math.max(maxWidthUsed, xPos);

        // Track max height for the current line
        currentLineHeight = Math.max(currentLineHeight, childHeight + vSpacing);
    }

    // Advance by the last line's height
    yPos += currentLineHeight;

    // Store final computed line height for onLayout
    this.line_height = currentLineHeight;

    int measuredWidth;
    if (widthMode == android.view.View.MeasureSpec.EXACTLY) {
        measuredWidth = android.view.View.MeasureSpec.getSize(widthMeasureSpec);
    } else {
        measuredWidth = maxWidthUsed + getPaddingRight();
        if (widthMode == android.view.View.MeasureSpec.AT_MOST) {
            measuredWidth = Math.min(measuredWidth, android.view.View.MeasureSpec.getSize(widthMeasureSpec));
        }
    }

    int measuredHeight;
    if (heightMode == android.view.View.MeasureSpec.EXACTLY) {
        measuredHeight = android.view.View.MeasureSpec.getSize(heightMeasureSpec);
    } else {
        measuredHeight = yPos + getPaddingBottom();
        if (heightMode == android.view.View.MeasureSpec.AT_MOST) {
            measuredHeight = Math.min(measuredHeight, android.view.View.MeasureSpec.getSize(heightMeasureSpec));
        }
    }

    setMeasuredDimension(measuredWidth, measuredHeight);
}