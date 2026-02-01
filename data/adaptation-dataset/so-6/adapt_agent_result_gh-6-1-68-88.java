protected void measureScrapChild(RecyclerView.Recycler recycler, int position, int widthSpec, int heightSpec, int[] measuredDimension) {
    if (measuredDimension == null || measuredDimension.length < 2) {
      return;
    }
    if (recycler == null || position < 0 || position >= getItemCount()) {
      measuredDimension[0] = 0;
      measuredDimension[1] = 0;
      return;
    }

    View child = null;
    try {
      child = recycler.getViewForPosition(position);
      if (child == null) {
        measuredDimension[0] = 0;
        measuredDimension[1] = 0;
        return;
      }

      RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
      if (lp == null) {
        lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        child.setLayoutParams(lp);
      }

      final int parentWidthPadding = getPaddingLeft() + getPaddingRight();
      final int parentHeightPadding = getPaddingTop() + getPaddingBottom();

      final int childWidthSpec = ViewGroup.getChildMeasureSpec(
          widthSpec,
          parentWidthPadding + lp.leftMargin + lp.rightMargin,
          lp.width);

      final int childHeightSpec = ViewGroup.getChildMeasureSpec(
          heightSpec,
          parentHeightPadding + lp.topMargin + lp.bottomMargin,
          lp.height);

      measureChildWithMargins(child, childWidthSpec, 0, childHeightSpec, 0);

      final int decoratedMeasuredWidth = getDecoratedMeasuredWidth(child);
      final int decoratedMeasuredHeight = getDecoratedMeasuredHeight(child);

      measuredDimension[0] = decoratedMeasuredWidth + lp.leftMargin + lp.rightMargin;
      measuredDimension[1] = decoratedMeasuredHeight + lp.topMargin + lp.bottomMargin;
    } catch (IndexOutOfBoundsException e) {
      measuredDimension[0] = 0;
      measuredDimension[1] = 0;
    } catch (IllegalStateException e) {
      measuredDimension[0] = 0;
      measuredDimension[1] = 0;
    } finally {
      if (child != null) {
        recycler.recycleView(child);
      }
    }
  }