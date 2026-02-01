// Reference: Based on a Stack Overflow answer for dynamically sizing a ListView based on its children.
public static void setListViewHeightBasedOnChildren(final android.widget.ListView listView) {
    final android.widget.ListAdapter listAdapter = listView.getAdapter();
    if (listAdapter == null) return; // pre-condition

    int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();

    for (int i = 0; i < listAdapter.getCount(); i++) {
        final android.view.View listItem = listAdapter.getView(i, null, listView);
        if (listItem instanceof android.view.ViewGroup) {
            listItem.setLayoutParams(new android.view.ViewGroup.LayoutParams(
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        listItem.measure(0, 0);
        totalHeight += listItem.getMeasuredHeight();
    }

    final android.view.ViewGroup.LayoutParams params = listView.getLayoutParams();
    params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
    listView.setLayoutParams(params);
}