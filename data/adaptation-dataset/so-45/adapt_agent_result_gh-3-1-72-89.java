public static int calculateTotalHeightOfListView(android.widget.ListView listView) {
    if (listView == null) {
        return 0;
    }

    android.widget.ListAdapter adapter = listView.getAdapter();
    if (adapter == null) {
        return 0;
    }

    int count = adapter.getCount();
    if (count == 0) {
        return 0;
    }

    int totalHeight = 0;
    android.view.View itemView = null;

    for (int i = 0; i < count; i++) {
        itemView = adapter.getView(i, itemView, listView);
        if (itemView == null) {
            continue;
        }

        itemView.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(0, android.view.View.MeasureSpec.UNSPECIFIED),
                android.view.View.MeasureSpec.makeMeasureSpec(0, android.view.View.MeasureSpec.UNSPECIFIED)
        );

        totalHeight += itemView.getMeasuredHeight();
    }

    totalHeight += listView.getDividerHeight() * (count - 1);
    return totalHeight;
}