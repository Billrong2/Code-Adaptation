private void ensureList() {
        if (mList != null) {
            return;
        }
        final View root = getView();
        if (root == null) {
            throw new IllegalStateException("Content view not yet created");
        }

        // Discover the ExpandableListView
        ExpandableListView list;
        if (root instanceof ExpandableListView) {
            list = (ExpandableListView) root;
            mListContainer = root;
        } else {
            View listView = root.findViewById(android.R.id.list);
            if (listView == null) {
                throw new IllegalStateException("Your content must have an ExpandableListView whose id attribute is android.R.id.list");
            }
            if (!(listView instanceof ExpandableListView)) {
                throw new IllegalStateException("View with id android.R.id.list is not an ExpandableListView");
            }
            list = (ExpandableListView) listView;
            mListContainer = list.getParent() instanceof View ? (View) list.getParent() : list;
        }
        mList = list;

        // Empty view handling
        View empty = root.findViewById(android.R.id.empty);
        if (empty != null) {
            if (empty instanceof TextView) {
                mStandardEmptyView = (TextView) empty;
                if (mEmptyText != null) {
                    mStandardEmptyView.setText(mEmptyText);
                }
                // Wrap the empty view in a ScrollView if needed
                if (empty.getParent() instanceof ScrollView) {
                    mEmptyViewScroll = (ScrollView) empty.getParent();
                } else {
                    mEmptyViewScroll = new ScrollView(getActivity());
                    mEmptyViewScroll.addView(empty);
                }
                mList.setEmptyView(mEmptyViewScroll);
            } else {
                // Fallback: use the provided empty view directly
                mList.setEmptyView(empty);
            }
        }

        // Wire listeners
        mList.setOnChildClickListener(this);
        mList.setOnGroupExpandListener(this);
        mList.setOnGroupCollapseListener(this);

        // Adapter / initial visibility state
        if (mAdapter != null) {
            mList.setAdapter(mAdapter);
            mListShown = true;
        } else {
            // No adapter yet: show loading state
            mListShown = false;
            setListShown(false, false);
        }

        // Request focus after layout
        mHandler.post(mRequestFocus);
    }