@Override
protected void onLayout(boolean changed, int l, int t, int r, int b) {
   // Guard against missing measurements
   if (currentRows == null || currentRows.isEmpty()) {
      return;
   }

   final int paddingLeft = Math.max(0, getPaddingLeft());
   final int paddingTop = Math.max(0, getPaddingTop());
   final int paddingRight = Math.max(0, getPaddingRight());
   final int availableRight = getMeasuredWidth() - paddingRight;
   final int safeHorizontalSpacing = Math.max(0, horizontalSpacing);
   final int safeVerticalSpacing = Math.max(0, verticalSpacing);

   int x = paddingLeft;
   int y = paddingTop;
   int rowIndex = 0;
   RowMeasurement currentRow = currentRows.get(rowIndex);

   for (View child : getLayoutChildren()) {
      if (child.getVisibility() == View.GONE) {
         continue;
      }

      final int childWidth = Math.max(0, child.getMeasuredWidth());
      final int childHeight = Math.max(0, child.getMeasuredHeight());

      // Wrap to next row if this child would exceed the right boundary
      if (x != paddingLeft && x + childWidth > availableRight && rowIndex < currentRows.size() - 1) {
         y += currentRow.getHeight() + safeVerticalSpacing;
         rowIndex++;
         currentRow = currentRows.get(rowIndex);
         x = paddingLeft;
      }

      // Layout the child using its measured size
      child.layout(x, y, x + childWidth, y + childHeight);

      // Advance x position for the next child
      x += childWidth + safeHorizontalSpacing;
   }
}