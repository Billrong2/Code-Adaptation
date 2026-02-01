public void drawVertical(final Canvas canvas, final RecyclerView parent) {
        if (canvas == null || parent == null || mDivider == null) {
            return;
        }

        final int childCount = parent.getChildCount();
        if (childCount == 0) {
            return;
        }

        final int parentTop = parent.getPaddingTop();
        final int parentBottom = parent.getHeight() - parent.getPaddingBottom();
        if (parentBottom <= parentTop) {
            return;
        }

        final int dividerThickness = mDivider.getIntrinsicHeight();

        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            if (child == null) {
                continue;
            }

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int dividerLeft = child.getRight() + params.rightMargin;
            final int dividerRight = dividerLeft + dividerThickness;

            // Ensure bounds stay within the parent's vertical drawing area
            final int top = Math.max(parentTop, 0);
            final int bottom = Math.min(parentBottom, parent.getHeight());

            mDivider.setBounds(dividerLeft, top, dividerRight, bottom);
            mDivider.draw(canvas);
        }
    }