@Override
public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state,
                      int widthSpec, int heightSpec) {
  // Guard against invalid inputs or empty adapter
  if (recycler == null || state == null || getItemCount() == 0) {
    super.onMeasure(recycler, state, widthSpec, heightSpec);
    return;
  }

  final int widthMode = View.MeasureSpec.getMode(widthSpec);
  final int heightMode = View.MeasureSpec.getMode(heightSpec);
  final int widthSize = View.MeasureSpec.getSize(widthSpec);
  final int heightSize = View.MeasureSpec.getSize(heightSpec);

  int totalWidth = 0;
  int totalHeight = 0;

  // Use a clearer local alias for the temp measurement array
  final int[] childDimensions = measuredDimension;

  // Measure each child to accumulate required size
  for (int i = 0; i < getItemCount(); i++) {
    // Use UNSPECIFIED specs with zero size to avoid invalid MeasureSpecs
    measureScrapChild(
        recycler,
        i,
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
        childDimensions);

    if (getOrientation() == HORIZONTAL) {
      totalWidth += childDimensions[0];
      if (i == 0) {
        totalHeight = childDimensions[1];
      }
    } else {
      totalHeight += childDimensions[1];
      if (i == 0) {
        totalWidth = childDimensions[0];
      }
    }
  }

  // Decide whether to apply custom wrap-content sizing or fall back
  boolean fitsInAvailableSpace =
      (widthMode != View.MeasureSpec.EXACTLY || totalWidth <= widthSize)
          && (heightMode != View.MeasureSpec.EXACTLY || totalHeight <= heightSize);

  if (!fitsInAvailableSpace) {
    // Content exceeds available space; allow normal measurement/scrolling
    super.onMeasure(recycler, state, widthSpec, heightSpec);
    return;
  }

  // Apply custom wrap-content sizing, relying on MeasureSpec modes
  int measuredWidth = totalWidth;
  int measuredHeight = totalHeight;

  if (widthMode == View.MeasureSpec.EXACTLY) {
    measuredWidth = widthSize;
  } else if (widthMode == View.MeasureSpec.AT_MOST) {
    measuredWidth = Math.min(totalWidth, widthSize);
  }

  if (heightMode == View.MeasureSpec.EXACTLY) {
    measuredHeight = heightSize;
  } else if (heightMode == View.MeasureSpec.AT_MOST) {
    measuredHeight = Math.min(totalHeight, heightSize);
  }

  setMeasuredDimension(measuredWidth, measuredHeight);
}