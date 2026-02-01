  @Override
  public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state,
      int widthSpec, int heightSpec) {

    // Code hardening: null checks and empty adapter handling
    if (recycler == null || state == null || getItemCount() <= 0) {
      super.onMeasure(recycler, state, widthSpec, heightSpec);
      return;
    }

    if (measuredDimension == null || measuredDimension.length < 2) {
      measuredDimension = new int[2];
    }

    final int widthMode = View.MeasureSpec.getMode(widthSpec);
    final int heightMode = View.MeasureSpec.getMode(heightSpec);
    final int widthSize = View.MeasureSpec.getSize(widthSpec);
    final int heightSize = View.MeasureSpec.getSize(heightSpec);

    int totalWidth = 0;
    int totalHeight = 0;

    final int itemCount = getItemCount();
    final int orientation = getOrientation();

    // Measure children with UNSPECIFIED specs to get natural size
    final int unspecifiedSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

    for (int i = 0; i < itemCount; i++) {
      if (orientation == HORIZONTAL) {
        measureScrapChild(recycler, i, unspecifiedSpec, unspecifiedSpec, measuredDimension);
        totalWidth += measuredDimension[0];
        if (i == 0) {
          totalHeight = measuredDimension[1];
        }
      } else {
        measureScrapChild(recycler, i, unspecifiedSpec, unspecifiedSpec, measuredDimension);
        totalHeight += measuredDimension[1];
        if (i == 0) {
          totalWidth = measuredDimension[0];
        }
      }
    }

    // If content exceeds available space, fall back to default measurement (preserve scrolling)
    if ((widthMode != View.MeasureSpec.UNSPECIFIED && totalWidth > widthSize)
        || (heightMode != View.MeasureSpec.UNSPECIFIED && totalHeight > heightSize)) {
      super.onMeasure(recycler, state, widthSpec, heightSpec);
      return;
    }

    // Respect EXACTLY mode, otherwise keep computed dimensions
    int finalWidth = totalWidth;
    int finalHeight = totalHeight;

    if (widthMode == View.MeasureSpec.EXACTLY) {
      finalWidth = widthSize;
    }
    if (heightMode == View.MeasureSpec.EXACTLY) {
      finalHeight = heightSize;
    }

    setMeasuredDimension(finalWidth, finalHeight);
  }