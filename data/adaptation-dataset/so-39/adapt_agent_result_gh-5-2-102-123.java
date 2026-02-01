@Override
protected void onLayout(boolean changed, int l, int t, int r, int b) {
    final int count = getChildCount();
    if (count == 0) {
        return;
    }

    final int paddingLeft = getPaddingLeft();
    final int paddingTop = getPaddingTop();

    final int containerWidth = Math.max(0, r - l);

    int currentLeft = paddingLeft;
    int currentTop = paddingTop;

    for (int i = 0; i < count; i++) {
        final View child = getChildAt(i);
        if (child == null || child.getVisibility() == GONE) {
            continue;
        }

        final ViewGroup.LayoutParams rawLp = child.getLayoutParams();
        if (!(rawLp instanceof PredicateLayout.LayoutParams)) {
            continue;
        }
        final PredicateLayout.LayoutParams lp = (PredicateLayout.LayoutParams) rawLp;

        final int childWidth = child.getMeasuredWidth();
        final int childHeight = child.getMeasuredHeight();

        if (currentLeft + childWidth > containerWidth && currentLeft > paddingLeft) {
            currentLeft = paddingLeft;
            currentTop += Math.max(0, line_height);
        }

        child.layout(
                currentLeft,
                currentTop,
                currentLeft + childWidth,
                currentTop + childHeight
        );

        currentLeft += childWidth + Math.max(0, lp.horizontal_spacing);
    }
}