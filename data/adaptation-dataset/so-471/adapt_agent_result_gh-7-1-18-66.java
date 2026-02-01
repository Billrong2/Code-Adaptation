  @Override
  public void onMeasure(android.support.v7.widget.RecyclerView.Recycler recycler,
      android.support.v7.widget.RecyclerView.State state, int widthSpec, int heightSpec) {

    // Code hardening: validate inputs
    if (recycler == null || state == null) {
      super.onMeasure(recycler, state, widthSpec, heightSpec);
      return;
    }

    final int itemCount = getItemCount();
    if (itemCount == 0) {
      super.onMeasure(recycler, state, widthSpec, heightSpec);
      return;
    }

    final int widthMode = android.view.View.MeasureSpec.getMode(widthSpec);
    final int heightMode = android.view.View.MeasureSpec.getMode(heightSpec);
    final int widthSize = android.view.View.MeasureSpec.getSize(widthSpec);
    final int heightSize = android.view.View.MeasureSpec.getSize(heightSpec);

    int accumulatedWidth = 0;
    int accumulatedHeight = 0;

    // Refactoring: clearer local alias for measurement buffer
    final int[] childMeasuredSize = (measuredDimension != null && measuredDimension.length >= 2)
        ? measuredDimension
        : new int[2];

    final int unspecified = android.view.View.MeasureSpec.makeMeasureSpec(0,
        android.view.View.MeasureSpec.UNSPECIFIED);

    for (int i = 0; i < itemCount; i++) {
      // Logic customization: always measure with UNSPECIFIED for both dimensions
      measureScrapChild(recycler, i, unspecified, unspecified, childMeasuredSize);

      if (getOrientation() == HORIZONTAL) {
        accumulatedWidth += childMeasuredSize[0];
        if (i == 0) {
          accumulatedHeight = childMeasuredSize[1];
        }
      } else {
        accumulatedHeight += childMeasuredSize[1];
        if (i == 0) {
          accumulatedWidth = childMeasuredSize[0];
        }
      }
    }

    // Logic customization: only wrap when both dimensions fit; otherwise defer to super
    if (accumulatedWidth < widthSize && accumulatedHeight < heightSize) {

      int measuredWidth = accumulatedWidth;
      int measuredHeight = accumulatedHeight;

      // Preserve EXACTLY handling and fall-through behavior
      switch (widthMode) {
        case android.view.View.MeasureSpec.EXACTLY:
          measuredWidth = widthSize;
        case android.view.View.MeasureSpec.AT_MOST:
        case android.view.View.MeasureSpec.UNSPECIFIED:
      }

      switch (heightMode) {
        case android.view.View.MeasureSpec.EXACTLY:
          measuredHeight = heightSize;
        case android.view.View.MeasureSpec.AT_MOST:
        case android.view.View.MeasureSpec.UNSPECIFIED:
      }

      setMeasuredDimension(measuredWidth, measuredHeight);
    } else {
      super.onMeasure(recycler, state, widthSpec, heightSpec);
    }
  }