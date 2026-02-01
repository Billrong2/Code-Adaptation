@Override
public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state,
                      int widthSpec, int heightSpec) {
  // Guard clauses
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

  final boolean isHorizontal = getOrientation() == HORIZONTAL;

  for (int i = 0; i < getItemCount(); i++) {
    measureScrapChild(recycler, i,
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
        measuredDimension);

    int childWidth = Math.max(0, measuredDimension[0]);
    int childHeight = Math.max(0, measuredDimension[1]);

    if (isHorizontal) {
      totalWidth += childWidth;
      if (i == 0) {
        totalHeight = childHeight;
      }
    } else {
      totalHeight += childHeight;
      if (i == 0) {
        totalWidth = childWidth;
      }
    }

    // Early exit if content already exceeds available space
    if ((widthSize > 0 && totalWidth > widthSize)
        || (heightSize > 0 && totalHeight > heightSize)) {
      super.onMeasure(recycler, state, widthSpec, heightSpec);
      return;
    }
  }

  int measuredWidth = totalWidth;
  int measuredHeight = totalHeight;

  // Honor EXACTLY modes
  if (widthMode == View.MeasureSpec.EXACTLY) {
    measuredWidth = widthSize;
  }
  if (heightMode == View.MeasureSpec.EXACTLY) {
    measuredHeight = heightSize;
  }

  setMeasuredDimension(measuredWidth, measuredHeight);
}