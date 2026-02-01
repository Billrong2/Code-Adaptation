@Override
protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    // Parent width must be constrained
    final int parentWidthMode = View.MeasureSpec.getMode(widthMeasureSpec);
    final int parentWidthSize = View.MeasureSpec.getSize(widthMeasureSpec);

    final int width = parentWidthSize - getPaddingLeft() - getPaddingRight();
    int height = View.MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();

    final int count = getChildCount();
    int line_height = 0;

    int xpos = getPaddingLeft();
    int ypos = getPaddingTop();

    for (int i = 0; i < count; i++) {
        final View child = getChildAt(i);
        if (child.getVisibility() != GONE) {
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();

            // Always measure child height with UNSPECIFIED; width remains AT_MOST
            child.measure(
                    View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.AT_MOST),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            );

            final int childw = child.getMeasuredWidth();
            line_height = Math.max(line_height, child.getMeasuredHeight() + lp.vertical_spacing);

            if (xpos + childw > width) {
                xpos = getPaddingLeft();
                ypos += line_height;
            }

            xpos += childw + lp.horizontal_spacing;
        }
    }

    this.line_height = line_height;

    final int parentHeightMode = View.MeasureSpec.getMode(heightMeasureSpec);
    if (parentHeightMode == View.MeasureSpec.UNSPECIFIED) {
        height = ypos + line_height;
    } else if (parentHeightMode == View.MeasureSpec.AT_MOST) {
        if (ypos + line_height < height) {
            height = ypos + line_height;
        }
    }

    setMeasuredDimension(width, height);
}