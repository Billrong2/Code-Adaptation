public void drawVertical(final Canvas canvas, final RecyclerView parent) {
        if (canvas == null || parent == null || mDivider == null) {
            return;
        }

        final int dividerWidth = mDivider.getIntrinsicHeight();
        if (dividerWidth <= 0) {
            return;
        }

        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();
        final int childCount = parent.getChildCount();

        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            if (child == null) {
                continue;
            }

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getRight() + params.rightMargin;
            final int right = left + dividerWidth;

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(canvas);
        }
    }