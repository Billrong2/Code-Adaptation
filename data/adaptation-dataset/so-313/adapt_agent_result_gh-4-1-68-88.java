private void measureScrapChild(final RecyclerView.Recycler recycler, final int position, final int parentWidthSpec, final int parentHeightSpec, final int[] outMeasuredDimension) {
    if (recycler == null || outMeasuredDimension == null || position < 0 || position >= getItemCount()) {
      if (outMeasuredDimension != null) {
        outMeasuredDimension[0] = 0;
        outMeasuredDimension[1] = 0;
      }
      return;
    }

    final View scrap = recycler.getViewForPosition(position);
    try {
      final RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) scrap.getLayoutParams();
      final android.graphics.Rect decorInsets = new android.graphics.Rect();
      getItemDecorInsetsForChild(scrap, decorInsets);

      final int horizontalInsets = getPaddingLeft() + getPaddingRight()
          + decorInsets.left + decorInsets.right
          + lp.leftMargin + lp.rightMargin;
      final int verticalInsets = getPaddingTop() + getPaddingBottom()
          + decorInsets.top + decorInsets.bottom
          + lp.topMargin + lp.bottomMargin;

      final int safeParentWidthSpec = Math.max(0, parentWidthSpec);
      final int safeParentHeightSpec = Math.max(0, parentHeightSpec);

      final int childWidthSpec = ViewGroup.getChildMeasureSpec(
          safeParentWidthSpec,
          horizontalInsets,
          lp.width);
      final int childHeightSpec = ViewGroup.getChildMeasureSpec(
          safeParentHeightSpec,
          verticalInsets,
          lp.height);

      measureChildWithMargins(scrap, childWidthSpec, childHeightSpec);

      outMeasuredDimension[0] = getDecoratedMeasuredWidth(scrap)
          + lp.leftMargin + lp.rightMargin;
      outMeasuredDimension[1] = getDecoratedMeasuredHeight(scrap)
          + lp.topMargin + lp.bottomMargin;
    } finally {
      recycler.recycleView(scrap);
    }
  }