private static void setListViewHeightBasedOnChildren(ListView listView) {
    // Hack adapted from Stack Overflow to correctly measure ListView height inside a ScrollView.
    // Source: <Stack Overflow link to be filled>
    if (listView == null) {
        return;
    }

    ListAdapter listAdapter = listView.getAdapter();
    if (listAdapter == null) {
        return;
    }

    final int itemCount = listAdapter.getCount();
    if (itemCount == 0) {
        return;
    }

    int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
    int totalHeight = 0;
    View itemView = null;

    for (int i = 0; i < itemCount; i++) {
        itemView = listAdapter.getView(i, itemView, listView);
        if (i == 0 && itemView != null) {
            itemView.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ActionBar.LayoutParams.WRAP_CONTENT));
        }
        if (itemView != null) {
            itemView.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += itemView.getMeasuredHeight();
        }
    }

    ViewGroup.LayoutParams params = listView.getLayoutParams();
    if (params == null) {
        return;
    }

    params.height = totalHeight + (listView.getDividerHeight() * (itemCount - 1));
    listView.setLayoutParams(params);
}