private void measureScrapChild(android.support.v7.widget.RecyclerView.Recycler recycler, int position, int widthSpec, int heightSpec, int[] measuredDimension) {
    if (measuredDimension == null || measuredDimension.length < 2) {
      return;
    }
    if (recycler == null || getItemCount() == 0 || position < 0 || position >= getItemCount()) {
      measuredDimension[0] = 0;
      measuredDimension[1] = 0;
      return;
    }

    android.view.View child = null;
    try {
      child = recycler.getViewForPosition(position);
      if (child == null) {
        measuredDimension[0] = 0;
        measuredDimension[1] = 0;
        return;
      }

      android.support.v7.widget.RecyclerView.LayoutParams lp =
          (android.support.v7.widget.RecyclerView.LayoutParams) child.getLayoutParams();

      // Account for item decorations
      android.graphics.Rect decorInsets = new android.graphics.Rect();
      calculateItemDecorationsForChild(child, decorInsets);

      final int parentWidthSpec = widthSpec;
      final int parentHeightSpec = heightSpec;

      final int widthUsed = getPaddingLeft() + getPaddingRight()
          + decorInsets.left + decorInsets.right
          + (lp != null ? lp.leftMargin + lp.rightMargin : 0);

      final int heightUsed = getPaddingTop() + getPaddingBottom()
          + decorInsets.top + decorInsets.bottom
          + (lp != null ? lp.topMargin + lp.bottomMargin : 0);

      final int childWidthSpec = android.view.ViewGroup.getChildMeasureSpec(
          parentWidthSpec, widthUsed, lp != null ? lp.width : android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

      final int childHeightSpec = android.view.ViewGroup.getChildMeasureSpec(
          parentHeightSpec, heightUsed, lp != null ? lp.height : android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

      measureChildWithMargins(child, childWidthSpec, 0, childHeightSpec, 0);

      int measuredWidth = getDecoratedMeasuredWidth(child);
      int measuredHeight = getDecoratedMeasuredHeight(child);

      if (lp != null) {
        measuredWidth += lp.leftMargin + lp.rightMargin;
        measuredHeight += lp.topMargin + lp.bottomMargin;
      }

      measuredDimension[0] = measuredWidth;
      measuredDimension[1] = measuredHeight;
    } finally {
      if (child != null) {
        recycler.recycleView(child);
      }
    }
  }