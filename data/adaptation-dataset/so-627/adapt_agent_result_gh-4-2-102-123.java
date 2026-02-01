    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        if (count <= 0) {
            return;
        }

        final int paddingLeft = Math.max(0, getPaddingLeft());
        final int paddingRight = Math.max(0, getPaddingRight());
        final int paddingTop = Math.max(0, getPaddingTop());

        final int parentWidth = r - l;
        if (parentWidth <= 0) {
            return; // nothing to lay out safely
        }

        final int contentWidth = Math.max(0, parentWidth - paddingLeft - paddingRight);
        final int rowHeight = Math.max(0, this.line_height);

        int x = paddingLeft;
        int y = paddingTop;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child == null || child.getVisibility() == GONE) {
                continue;
            }

            final ViewGroup.LayoutParams rawLp = child.getLayoutParams();
            if (!(rawLp instanceof LayoutParams)) {
                continue; // skip invalid layout params
            }
            final LayoutParams lp = (LayoutParams) rawLp;

            final int childWidth = child.getMeasuredWidth();
            final int childHeight = child.getMeasuredHeight();

            // Wrap to next line if this child would exceed available width
            if (x > paddingLeft && x + childWidth > paddingLeft + contentWidth) {
                x = paddingLeft;
                y += rowHeight;
            }

            child.layout(x, y, x + childWidth, y + childHeight);

            // Advance x by measured width plus horizontal spacing
            x += childWidth + Math.max(0, lp.horizontal_spacing);
        }
    }