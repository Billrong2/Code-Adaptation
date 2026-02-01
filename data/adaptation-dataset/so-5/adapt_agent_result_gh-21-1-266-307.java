private void ensureList() {
	if (mList != null) {
		return;
	}
	final View root = getView();
	if (root == null) {
		throw new IllegalStateException("Content view not yet created; cannot ensure list");
	}

	// Identify list container
	mListContainer = root;

	// Locate ExpandableListView either as root or via android.R.id.list
	View listView;
	if (root instanceof ExpandableListView) {
		listView = root;
	} else {
		listView = root.findViewById(android.R.id.list);
	}
	if (listView == null) {
		throw new IllegalStateException("Your content must have an ExpandableListView whose id attribute is 'android.R.id.list'");
	}
	if (!(listView instanceof ExpandableListView)) {
		throw new IllegalStateException("View with id 'android.R.id.list' is not an ExpandableListView");
	}
	mList = (ExpandableListView) listView;

	// Empty view handling
	View emptyView = root.findViewById(android.R.id.empty);
	if (emptyView == null) {
		View standardEmpty = root.findViewById(INTERNAL_EMPTY_ID);
		if (standardEmpty instanceof TextView) {
			mStandardEmptyView = (TextView) standardEmpty;
			emptyView = mStandardEmptyView;
		}
	}
	if (emptyView != null) {
		mList.setEmptyView(emptyView);
	}

	// Attach listeners
	mList.setOnChildClickListener(this);
	mList.setOnGroupExpandListener(this);
	mList.setOnGroupCollapseListener(this);
	mList.setOnItemLongClickListener(mOnLongClickListener);

	// Initial state: list is considered shown once initialized
	mListShown = true;

	// Adapter or progress handling
	if (mAdapter != null) {
		mList.setAdapter((ExpandableListAdapter) null);
		mList.setAdapter(mAdapter);
	} else {
		// No adapter yet; start in progress state without animation
		setListShown(false, false);
	}

	// Request focus after setup
	mHandler.post(mRequestFocus);
}