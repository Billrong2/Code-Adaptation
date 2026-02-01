@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	// Root container
	android.widget.FrameLayout root = new android.widget.FrameLayout(getActivity());
	root.setLayoutParams(new android.widget.FrameLayout.LayoutParams(
			android.view.ViewGroup.LayoutParams.MATCH_PARENT,
			android.view.ViewGroup.LayoutParams.MATCH_PARENT));

	// Standard empty view (centered TextView)
	android.widget.TextView empty = new android.widget.TextView(getActivity());	empty.setId(INTERNAL_EMPTY_ID);	empty.setGravity(android.view.Gravity.CENTER);	empty.setLayoutParams(new android.widget.FrameLayout.LayoutParams(
			android.view.ViewGroup.LayoutParams.MATCH_PARENT,
			android.view.ViewGroup.LayoutParams.MATCH_PARENT));	root.addView(empty);

	// ExpandableListView filling parent
	android.widget.ExpandableListView list = new android.widget.ExpandableListView(getActivity());	list.setId(android.R.id.list);	list.setLayoutParams(new android.widget.FrameLayout.LayoutParams(
			android.view.ViewGroup.LayoutParams.MATCH_PARENT,
			android.view.ViewGroup.LayoutParams.MATCH_PARENT));
	list.setEmptyView(empty);

	// Attach ExpandableListView-specific listeners to this fragment
	list.setOnChildClickListener(this);	list.setOnGroupExpandListener(this);	list.setOnGroupCollapseListener(this);

	root.addView(list);
	return root;
}