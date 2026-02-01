private void ensureList() {
        if (mList != null) {
            return;
        }
        View root = getView();
        if (root == null) {
            throw new IllegalStateException("Fragment content view not created yet");
        }

        ExpandableListView list;
        if (root instanceof ExpandableListView) {
            list = (ExpandableListView) root;
        } else {
            View listView = root.findViewById(android.R.id.list);
            if (listView == null) {
                throw new IllegalStateException("Your content must have an ExpandableListView whose id attribute is android.R.id.list");
            }
            if (!(listView instanceof ExpandableListView)) {
                throw new IllegalStateException("View with id android.R.id.list is not an ExpandableListView");
            }
            list = (ExpandableListView) listView;
        }

        mList = list;

        // Empty view handling
        View empty = root.findViewById(android.R.id.empty);
        if (empty instanceof TextView) {
            mStandardEmptyView = (TextView) empty;
        }
        if (empty != null) {
            mList.setEmptyView(empty);
        }

        // Attach listeners
        mList.setOnItemClickListener(mOnClickListener);
        mList.setOnChildClickListener(mOnChildClickListener);
        mList.setOnGroupExpandListener(this);
        mList.setOnGroupCollapseListener(this);
        mList.setOnCreateContextMenuListener(this);

        // Initial state
        mListShown = true;
        if (mAdapter != null) {
            mList.setAdapter(mAdapter);
        } else {
            setListShown(false, false);
        }

        // Request focus after layout
        mHandler.post(mRequestFocus);
    }