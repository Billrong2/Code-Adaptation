@Override
protected void onLayout(boolean changed, int l, int t, int r, int b) {
  // Contract: children have already been measured and row breaks computed in onMeasure.
  final List<RowMeasurement> rows = currentRows;
  if (rows == null || rows.isEmpty()) {
    return;
  }

  final List<View> children = getLayoutChildren();
  if (children == null || children.isEmpty()) {
    return;
  }

  final int paddingLeft = getPaddingLeft();
  final int paddingRight = getPaddingRight();
  final int paddingTop = getPaddingTop();

  final int measuredWidth = getMeasuredWidth();
  final int rightBoundary = Math.max(paddingLeft, measuredWidth - paddingRight);

  final boolean isRtl = android.os.Build.VERSION.SDK_INT >= 17
      && getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;

  int x = isRtl ? rightBoundary : paddingLeft;
  int y = paddingTop;

  int rowIndex = 0;
  RowMeasurement currentRow = rows.get(rowIndex);
  int childIndex = 0;

  while (childIndex < children.size() && rowIndex < rows.size()) {
    final View child = children.get(childIndex);
    if (child == null) {
      childIndex++;
      continue;
    }

    final int childWidth = child.getMeasuredWidth();
    final int childHeight = child.getMeasuredHeight();

    // Check if the child fits in the current row; if not, wrap to next row.
    final boolean exceeds;
    if (isRtl) {
      final int nextX = x - (x == rightBoundary ? childWidth : (horizontalSpacing + childWidth));
      exceeds = nextX < paddingLeft;
    } else {
      final int nextX = x + (x == paddingLeft ? childWidth : (horizontalSpacing + childWidth));
      exceeds = nextX > rightBoundary;
    }

    if (exceeds && rowIndex < rows.size() - 1) {
      // Move to next row.
      y += currentRow.getHeight() + verticalSpacing;
      rowIndex++;
      currentRow = rows.get(rowIndex);
      x = isRtl ? rightBoundary : paddingLeft;
      continue;
    }

    // Vertically center the child within the row.
    final int childTop = y + Math.max(0, (currentRow.getHeight() - childHeight) / 2);

    if (isRtl) {
      final int childRight = (x == rightBoundary) ? x : x - horizontalSpacing;
      final int childLeft = childRight - childWidth;
      child.layout(childLeft, childTop, childRight, childTop + childHeight);
      x = childLeft;
    } else {
      final int childLeft = (x == paddingLeft) ? x : x + horizontalSpacing;
      final int childRight = childLeft + childWidth;
      child.layout(childLeft, childTop, childRight, childTop + childHeight);
      x = childRight;
    }

    childIndex++;
  }
}