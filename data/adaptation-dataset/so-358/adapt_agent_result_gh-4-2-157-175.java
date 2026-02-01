public static void setListViewHeightBasedOnChildren(ListView listView) {
    if (listView == null) {
        return;
    }
    ListAdapter listAdapter = listView.getAdapter();
    if (listAdapter == null) {
        return;
    }
    int itemCount = listAdapter.getCount();
    if (itemCount <= 0) {
        ViewGroup.LayoutParams emptyParams = listView.getLayoutParams();
        if (emptyParams != null) {
            emptyParams.height = listView.getPaddingTop() + listView.getPaddingBottom();
            listView.setLayoutParams(emptyParams);
        }
        return;
    }

    int contentHeight = 0;
    for (int i = 0; i < itemCount; i++) {
        // Always obtain a fresh view for accurate measurement
        View itemView = listAdapter.getView(i, null, listView);
        if (itemView != null) {
            // Ensure layout params exist and are wrap_content before measuring
            ViewGroup.LayoutParams itemParams = itemView.getLayoutParams();
            if (itemParams == null) {
                itemParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            } else if (itemView instanceof ViewGroup) {
                itemParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                itemParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
            itemView.setLayoutParams(itemParams);

            // Measure with unspecified specs to avoid width constraints
            itemView.measure(0, 0);
            contentHeight += itemView.getMeasuredHeight();
        }
    }

    int dividerHeight = listView.getDividerHeight();
    int totalDividerHeight = dividerHeight * Math.max(0, itemCount - 1);

    int totalHeight = contentHeight
            + totalDividerHeight
            + listView.getPaddingTop()
            + listView.getPaddingBottom();

    ViewGroup.LayoutParams params = listView.getLayoutParams();
    if (params != null) {
        params.height = totalHeight;
        listView.setLayoutParams(params);
    }
}