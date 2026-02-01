  @Override
  public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec,
      int heightSpec) {
    // Guard against empty adapter
    if (getItemCount() == 0) {
      super.onMeasure(recycler, state, widthSpec, heightSpec);
      return;
    }

    final int widthMode = View.MeasureSpec.getMode(widthSpec);
    final int heightMode = View.MeasureSpec.getMode(heightSpec);
    final int widthSize = View.MeasureSpec.getSize(widthSpec);
    final int heightSize = View.MeasureSpec.getSize(heightSpec);

    int width = getPaddingLeft() + getPaddingRight();
    int height = getPaddingTop() + getPaddingBottom();

    View child = null;
    try {
      // Measure each child based on parent specs, margins, padding, and decorations
      for (int i = 0; i < getItemCount(); i++) {
        child = recycler.getViewForPosition(i);
        addView(child);

        RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
        if (lp == null) {
          lp = (RecyclerView.LayoutParams) generateDefaultLayoutParams();
          child.setLayoutParams(lp);
        }

        // Account for item decorations
        android.graphics.Rect decorInsets = new android.graphics.Rect();
        calculateItemDecorationsForChild(child, decorInsets);

        final int childWidthSpec = getChildMeasureSpec(
            widthSpec,
            getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin
                + decorInsets.left + decorInsets.right,
            lp.width);

        final int childHeightSpec = getChildMeasureSpec(
            heightSpec,
            getPaddingTop() + getPaddingBottom() + lp.topMargin + lp.bottomMargin
                + decorInsets.top + decorInsets.bottom,
            lp.height);

        child.measure(childWidthSpec, childHeightSpec);

        final int decoratedMeasuredWidth = getDecoratedMeasuredWidth(child)
            + lp.leftMargin + lp.rightMargin;
        final int decoratedMeasuredHeight = getDecoratedMeasuredHeight(child)
            + lp.topMargin + lp.bottomMargin;

        if (getOrientation() == HORIZONTAL) {
          width += decoratedMeasuredWidth;
          height = Math.max(height, decoratedMeasuredHeight + getPaddingTop() + getPaddingBottom());
        } else {
          height += decoratedMeasuredHeight;
          width = Math.max(width, decoratedMeasuredWidth + getPaddingLeft() + getPaddingRight());
        }

        // Detach and recycle the temporary child
        detachAndScrapView(child, recycler);
        child = null;
      }
    } finally {
      // Ensure no leaked temporary view
      if (child != null) {
        detachAndScrapView(child, recycler);
      }
    }

    // If content exceeds available space, fall back to default measuring (enables scrolling)
    if (height < heightSize && width < widthSize) {
      switch (widthMode) {
        case View.MeasureSpec.EXACTLY:
          width = widthSize;
          break;
        case View.MeasureSpec.AT_MOST:
          width = Math.min(width, widthSize);
          break;
        case View.MeasureSpec.UNSPECIFIED:
        default:
          break;
      }

      switch (heightMode) {
        case View.MeasureSpec.EXACTLY:
          height = heightSize;
          break;
        case View.MeasureSpec.AT_MOST:
          height = Math.min(height, heightSize);
          break;
        case View.MeasureSpec.UNSPECIFIED:
        default:
          break;
      }

      setMeasuredDimension(width, height);
    } else {
      super.onMeasure(recycler, state, widthSpec, heightSpec);
    }
  }