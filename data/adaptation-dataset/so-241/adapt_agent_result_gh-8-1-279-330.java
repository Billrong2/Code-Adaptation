private void ensureList() {
    if (mList != null) {
        return;
    }
    View root = getView();
    if (root == null) {
        throw new IllegalStateException("Content view not yet created");
    }

    if (root instanceof ExpandableListView) {
        mList = (ExpandableListView) root;
    } else {
        // Discover standard empty TextView using framework id
        View emptyCandidate = root.findViewById(android.R.id.empty);
        if (emptyCandidate instanceof TextView) {
            mStandardEmptyView = (TextView) emptyCandidate;
        }

        // Initialize list container from concrete layout container
        mListContainer = root.findViewById(com.money.manager.ex.R.id.container);

        View rawListView = root.findViewById(android.R.id.list);
        if (!(rawListView instanceof ExpandableListView)) {
            if (rawListView == null) {
                throw new RuntimeException(
                        "Your content must have a ExpandableListView whose id attribute is " +
                                "'android.R.id.list'");
            }
            throw new RuntimeException(
                    "Content has view with id attribute 'android.R.id.list' " +
                            "that is not a ExpandableListView class");
        }
        mList = (ExpandableListView) rawListView;

        // Wrap the standard empty view in a ScrollView if present
        if (mStandardEmptyView != null) {
            View parent = (View) mStandardEmptyView.getParent();
            if (parent instanceof ScrollView) {
                mEmptyViewScroll = (ScrollView) parent;
            } else if (parent instanceof ViewGroup) {
                ViewGroup parentGroup = (ViewGroup) parent;
                int index = parentGroup.indexOfChild(mStandardEmptyView);
                parentGroup.removeView(mStandardEmptyView);
                mEmptyViewScroll = new ScrollView(root.getContext());
                mEmptyViewScroll.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                mEmptyViewScroll.addView(mStandardEmptyView,
                        new ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));
                parentGroup.addView(mEmptyViewScroll, index);
            }

            // Assign scrollable empty view to the list
            if (mEmptyViewScroll != null) {
                mList.setEmptyView(mEmptyViewScroll);
                mEmptyViewScroll.setVisibility(View.GONE);
            }
        }
    }

    mListShown = true;
    mList.setOnItemClickListener(mOnClickListener);
    if (mAdapter != null) {
        setListAdapter(mAdapter);
    } else {
        // No adapter yet: start with the list hidden (loading state)
        setListShown(false, false);
    }
    mHandler.post(mRequestFocus);
}