@Override
protected void onLayout(boolean changed, int l, int t, int r, int b) {
    final int count = getChildCount();
    if (count == 0) {
        return;
    }

    final int paddingLeft = Math.max(0, getPaddingLeft());
    final int paddingTop = Math.max(0, getPaddingTop());
    final int parentWidth = Math.max(0, r - l);

    int curX = paddingLeft;
    int curY = paddingTop;
    int lineHeight = 0;

    for (int i = 0; i < count; i++) {
        final View child = getChildAt(i);
        if (child == null || child.getVisibility() == GONE) {
            continue;
        }

        ViewGroup.LayoutParams rawLp = child.getLayoutParams();
        if (!(rawLp instanceof LayoutParams)) {
            continue; // defensive: unexpected LayoutParams
        }
        final LayoutParams lp = (LayoutParams) rawLp;

        final int childWidth = child.getMeasuredWidth();
        final int childHeight = child.getMeasuredHeight();
        if (childWidth <= 0 || childHeight <= 0) {
            continue;
        }

        final int horizontalSpacing = Math.max(0, lp.horizontal_spacing);
        final int verticalSpacing = Math.max(0, lp.vertical_spacing);

        // Wrap to next line if this child would exceed available width
        if (curX + childWidth > parentWidth) {
            curX = paddingLeft;
            curY += lineHeight;
            lineHeight = 0;
        }

        // Layout the child
        child.layout(curX, curY, curX + childWidth, curY + childHeight);

        // Advance cursor and update line height
        curX += childWidth + horizontalSpacing;
        lineHeight = Math.max(lineHeight, childHeight + verticalSpacing);
    }
}