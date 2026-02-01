@Override
protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    // Custom measurement is required so a ListView can size itself correctly when nested in a scrollable parent.
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    int newHeight = 0;
    final int heightMode = android.view.View.MeasureSpec.getMode(heightMeasureSpec);
    final int heightSize = android.view.View.MeasureSpec.getSize(heightMeasureSpec);

    if (heightMode != android.view.View.MeasureSpec.EXACTLY) {
        final android.widget.ListAdapter listAdapter = getAdapter();
        if (listAdapter != null && !listAdapter.isEmpty()) {
            int listPosition = 0;
            android.view.View convertView = null;
            for (listPosition = 0;
                 listPosition < listAdapter.getCount()
                         && listPosition < MAXIMUM_LIST_ITEMS_VIEWABLE;
                 listPosition++) {
                final android.view.View listItem = listAdapter.getView(listPosition, convertView, this);
                convertView = listItem;
                if (listItem == null) {
                    continue;
                }
                if (listItem instanceof android.view.ViewGroup) {
                    listItem.setLayoutParams(new android.view.ViewGroup.LayoutParams(
                            android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                            android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
                }
                listItem.measure(widthMeasureSpec, heightMeasureSpec);
                newHeight += listItem.getMeasuredHeight();
            }
            newHeight += getDividerHeight() * listPosition;
        }
        if (heightMode == android.view.View.MeasureSpec.AT_MOST && newHeight > heightSize) {
            newHeight = heightSize;
        }
    } else {
        newHeight = getMeasuredHeight();
    }

    setMeasuredDimension(getMeasuredWidth(), newHeight);
}