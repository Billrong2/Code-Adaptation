@Override
protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
  final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
  final int heightMode = MeasureSpec.getMode(heightMeasureSpec);

  int maxInternalWidth = MeasureSpec.getSize(widthMeasureSpec) - getHorizontalPadding();
  int maxInternalHeight = MeasureSpec.getSize(heightMeasureSpec) - getVerticalPadding();
  if (maxInternalWidth < 0) {
    maxInternalWidth = 0;
  }
  if (maxInternalHeight < 0) {
    maxInternalHeight = 0;
  }

  final List<View> layoutChildren = getLayoutChildren();
  final List<RowMeasurement> rows = new ArrayList<RowMeasurement>();
  RowMeasurement currentRow = new RowMeasurement(maxInternalWidth, widthMode);
  rows.add(currentRow);

  if (layoutChildren != null) {
    for (final View child : layoutChildren) {
      if (child == null) {
        continue;
      }

      final LayoutParams childLayoutParams = child.getLayoutParams();
      final int childWidthSpec = createChildMeasureSpec(childLayoutParams.width, maxInternalWidth, widthMode);
      final int childHeightSpec = createChildMeasureSpec(childLayoutParams.height, maxInternalHeight, heightMode);

      child.measure(childWidthSpec, childHeightSpec);

      final int childWidth = child.getMeasuredWidth();
      final int childHeight = child.getMeasuredHeight();

      if (currentRow.wouldExceedMax(childWidth)) {
        currentRow = new RowMeasurement(maxInternalWidth, widthMode);
        rows.add(currentRow);
      }

      currentRow.addChildDimensions(childWidth, childHeight);
    }
  }

  int longestRowWidth = 0;
  int totalRowHeight = 0;
  for (int index = 0; index < rows.size(); index++) {
    final RowMeasurement row = rows.get(index);
    totalRowHeight += row.getHeight();
    if (index < rows.size() - 1) {
      totalRowHeight += verticalSpacing;
    }
    longestRowWidth = Math.max(longestRowWidth, row.getWidth());
  }

  final int measuredWidth = widthMode == MeasureSpec.EXACTLY
      ? MeasureSpec.getSize(widthMeasureSpec)
      : longestRowWidth + getHorizontalPadding();

  final int measuredHeight = heightMode == MeasureSpec.EXACTLY
      ? MeasureSpec.getSize(heightMeasureSpec)
      : totalRowHeight + getVerticalPadding();

  setMeasuredDimension(measuredWidth, measuredHeight);
  currentRows = Collections.unmodifiableList(rows);
}