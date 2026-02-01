private void drawVertical(Canvas c, RecyclerView parent) {
        if (divider == null || parent == null) {
            return;
        }
        final RecyclerView.LayoutManager manager = parent.getLayoutManager()
                ;
        if (manager == null) {
            return;
        }
        final int childCount = parent.getChildCount();
        if (childCount <= 0) {
            return;
        }

        final int lastDecoratedChild = getLastDecoratedChild(parent);
        final int dividerHeight = divider.getIntrinsicHeight();
        if (dividerHeight <= 0) {
            return;
        }

        for (int i = 0; i < lastDecoratedChild; i++) {
            final View child = parent.getChildAt(i);
            if (child == null) {
                continue;
            }

            final int ty = (int) (child.getTranslationY() + 0.5f);
            final int tx = (int) (child.getTranslationX() + 0.5f);

            final int decoratedBottom = manager.getDecoratedBottom(child);
            final int decoratedLeft = manager.getDecoratedLeft(child);
            final int decoratedRight = manager.getDecoratedRight(child);

            if (decoratedRight <= decoratedLeft) {
                continue;
            }

            int dividerTop = decoratedBottom + ty;
            int dividerBottom = dividerTop + dividerHeight;
            int dividerLeft = decoratedLeft + tx;
            int dividerRight = decoratedRight + tx;

            // Clamp to canvas bounds to be safe
            dividerLeft = Math.max(0, dividerLeft);
            dividerTop = Math.max(0, dividerTop);
            dividerRight = Math.min(c.getWidth(), dividerRight);
            dividerBottom = Math.min(c.getHeight(), dividerBottom);

            if (dividerRight > dividerLeft && dividerBottom > dividerTop) {
                divider.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom);
                divider.draw(c);
            }
        }
    }