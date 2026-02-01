// Reference: https://stackoverflow.com/questions/17693578/android-set-listview-height-based-on-children
public static void setListViewHeightBasedOnChildren(final android.widget.ListView listView) {
    android.widget.ListAdapter listAdapter = listView.getAdapter();
    if (listAdapter == null) {
        return;
    }

    int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
    for (int i = 0; i < listAdapter.getCount(); i++) {
        android.view.View listItem = listAdapter.getView(i, null, listView);
        if (listItem instanceof android.view.ViewGroup)
            listItem.setLayoutParams(new android.view.ViewGroup.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
        listItem.measure(0, 0);
        totalHeight += listItem.getMeasuredHeight();
    }

    android.view.ViewGroup.LayoutParams params = listView.getLayoutParams();
    params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
    listView.setLayoutParams(params);
}