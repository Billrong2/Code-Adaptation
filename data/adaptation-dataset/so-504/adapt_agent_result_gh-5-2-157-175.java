private static void setListViewHeightBasedOnChildren(ListView listView) {
    if (listView == null) {
        return;
    }
    ListAdapter listAdapter = listView.getAdapter();
    if (listAdapter == null) {
        return;
    }

    final int itemCount = listAdapter.getCount();
    int totalHeight = 0;

    final int widthSpec = android.view.View.MeasureSpec.makeMeasureSpec(0, android.view.View.MeasureSpec.UNSPECIFIED);
    final int heightSpec = android.view.View.MeasureSpec.makeMeasureSpec(0, android.view.View.MeasureSpec.UNSPECIFIED);

    for (int i = 0; i < itemCount; i++) {
        View itemView = listAdapter.getView(i, null, listView);
        if (itemView == null) {
            continue;
        }

        if (itemView instanceof ViewGroup) {
            ViewGroup.LayoutParams lp = itemView.getLayoutParams();
            if (lp == null) {
                lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            } else {
                lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
            itemView.setLayoutParams(lp);
        }

        itemView.measure(widthSpec, heightSpec);
        totalHeight += itemView.getMeasuredHeight();
    }

    totalHeight += listView.getPaddingTop() + listView.getPaddingBottom();

    if (itemCount > 1) {
        totalHeight += listView.getDividerHeight() * (itemCount - 1);
    }

    ViewGroup.LayoutParams params = listView.getLayoutParams();
    if (params == null) {
        params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
    params.height = totalHeight;
    listView.setLayoutParams(params);
}