private void measureScrapChild(android.support.v7.widget.RecyclerView.Recycler recycler, int position, int widthSpec, int heightSpec, int[] measuredDimension) {
    if (recycler == null || measuredDimension == null || measuredDimension.length < 2) {
      return;
    }
    if (getItemCount() == 0 || position < 0 || position >= getItemCount()) {
      measuredDimension[0] = 0;
      measuredDimension[1] = 0;
      return;
    }

    android.view.View view = null;
    try {
      view = recycler.getViewForPosition(position);
      if (view == null) {
        measuredDimension[0] = 0;
        measuredDimension[1] = 0;
        return;
      }

      // Ensure LayoutParams exist
      android.support.v7.widget.RecyclerView.LayoutParams lp =
          (android.support.v7.widget.RecyclerView.LayoutParams) view.getLayoutParams();
      if (lp == null) {
        lp = new android.support.v7.widget.RecyclerView.LayoutParams(
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
      }

      // Build child measure specs accounting for parent padding and margins
      final int horizontalPadding = getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin;
      final int verticalPadding = getPaddingTop() + getPaddingBottom() + lp.topMargin + lp.bottomMargin;

      final int childWidthSpec = android.view.ViewGroup.getChildMeasureSpec(
          widthSpec, horizontalPadding, lp.width);
      final int childHeightSpec = android.view.ViewGroup.getChildMeasureSpec(
          heightSpec, verticalPadding, lp.height);

      // Temporarily add and measure the view so item decorations are included
      addView(view);
      measureChildWithMargins(view, childWidthSpec, childHeightSpec);

      final int decoratedWidth = getDecoratedMeasuredWidth(view) + lp.leftMargin + lp.rightMargin;
      final int decoratedHeight = getDecoratedMeasuredHeight(view) + lp.topMargin + lp.bottomMargin;

      measuredDimension[0] = decoratedWidth;
      measuredDimension[1] = decoratedHeight;
    } finally {
      if (view != null) {
        // Remove and recycle the temporary view
        removeAndRecycleView(view, recycler);
      }
    }
  }