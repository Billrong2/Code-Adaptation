  @Override
  public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
    // Null-safety guards
    if (recycler == null || state == null) {
      super.onMeasure(recycler, state, widthSpec, heightSpec);
      return;
    }

    final int itemCount = getItemCount();
    if (itemCount <= 0) {
      super.onMeasure(recycler, state, widthSpec, heightSpec);
      return;
    }

    final int widthMode = View.MeasureSpec.getMode(widthSpec);
    final int heightMode = View.MeasureSpec.getMode(heightSpec);
    final int widthSize = View.MeasureSpec.getSize(widthSpec);
    final int heightSize = View.MeasureSpec.getSize(heightSpec);

    int width = 0;
    int height = 0;

    // Reuse shared measuredDimension array safely
    if (measuredDimension == null || measuredDimension.length < 2) {
      measuredDimension = new int[2];
    }

    for (int i = 0; i < itemCount; i++) {
      // Always measure children with UNSPECIFIED specs for both dimensions
      // Note: using loop index as size value (functionally unconstrained)
      final int childWidthSpec = View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED);
      final int childHeightSpec = View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED);

      measureScrapChild(recycler, i, childWidthSpec, childHeightSpec, measuredDimension);

      // Accumulate dimensions based on orientation
      if (getOrientation() == LinearLayoutManager.HORIZONTAL) {
        width += measuredDimension[0];
        if (i == 0) {
          height = measuredDimension[1];
        }
      } else {
        height += measuredDimension[1];
        if (i == 0) {
          width = measuredDimension[0];
        }
      }
    }

    // If content fits within parent bounds, respect MeasureSpec; otherwise fall back to super for scrolling
    if (height < heightSize && width < widthSize) {
      switch (widthMode) {
        case View.MeasureSpec.EXACTLY:
          width = widthSize;
        case View.MeasureSpec.AT_MOST:
        case View.MeasureSpec.UNSPECIFIED:
          break;
      }

      switch (heightMode) {
        case View.MeasureSpec.EXACTLY:
          height = heightSize;
        case View.MeasureSpec.AT_MOST:
        case View.MeasureSpec.UNSPECIFIED:
          break;
      }

      setMeasuredDimension(width, height);
    } else {
      super.onMeasure(recycler, state, widthSpec, heightSpec);
    }
  }