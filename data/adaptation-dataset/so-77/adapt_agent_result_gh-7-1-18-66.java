@Override
public void onMeasure(android.support.v7.widget.RecyclerView.Recycler recycler,
                      android.support.v7.widget.RecyclerView.State state,
                      int widthSpec,
                      int heightSpec) {
  if (recycler == null || state == null) {
    super.onMeasure(recycler, state, widthSpec, heightSpec);
    return;
  }

  if (getItemCount() == 0) {
    super.onMeasure(recycler, state, widthSpec, heightSpec);
    return;
  }

  if (measuredDimension == null || measuredDimension.length < 2) {
    measuredDimension = new int[2];
  }

  final int widthMode = android.view.View.MeasureSpec.getMode(widthSpec);
  final int heightMode = android.view.View.MeasureSpec.getMode(heightSpec);
  final int widthSize = android.view.View.MeasureSpec.getSize(widthSpec);
  final int heightSize = android.view.View.MeasureSpec.getSize(heightSpec);

  int width = 0;
  int height = 0;

  for (int i = 0; i < getItemCount(); i++) {
    measureScrapChild(
        recycler,
        i,
        android.view.View.MeasureSpec.makeMeasureSpec(i, android.view.View.MeasureSpec.UNSPECIFIED),
        android.view.View.MeasureSpec.makeMeasureSpec(i, android.view.View.MeasureSpec.UNSPECIFIED),
        measuredDimension);

    if (getOrientation() == HORIZONTAL) {
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

  // If child view is more than screen size, fall back to default measurement to allow scrolling
  if (height < heightSize && width < widthSize) {
    switch (widthMode) {
      case android.view.View.MeasureSpec.EXACTLY:
        width = widthSize;
        break;
      case android.view.View.MeasureSpec.AT_MOST:
      case android.view.View.MeasureSpec.UNSPECIFIED:
        break;
    }

    switch (heightMode) {
      case android.view.View.MeasureSpec.EXACTLY:
        height = heightSize;
        break;
      case android.view.View.MeasureSpec.AT_MOST:
      case android.view.View.MeasureSpec.UNSPECIFIED:
        break;
    }

    setMeasuredDimension(width, height);
  } else {
    super.onMeasure(recycler, state, widthSpec, heightSpec);
  }
}