@Override
@SuppressWarnings("ResourceType")
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Programmatically construct the default view hierarchy for this fragment.
    // The hierarchy always consists of a root FrameLayout containing:
    // 1) A centered TextView used as the standard empty view (INTERNAL_EMPTY_ID)
    // 2) An ExpandableListView with id android.R.id.list filling the parent
    final android.content.Context context = getActivity();

    final FrameLayout root = new FrameLayout(context);
    root.setLayoutParams(new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));

    // Standard empty view shown when the list has no data
    final TextView emptyView = new TextView(context);
    emptyView.setId(INTERNAL_EMPTY_ID);
    emptyView.setGravity(Gravity.CENTER);
    emptyView.setVisibility(View.GONE);
    emptyView.setLayoutParams(new FrameLayout.LayoutParams(
            MATCH_PARENT,
            MATCH_PARENT
    ));

    // Expandable list view that fills the parent
    final ExpandableListView listView = new ExpandableListView(context);
    listView.setId(android.R.id.list);
    listView.setSelectorOnTop(false);
    listView.setLayoutParams(new FrameLayout.LayoutParams(
            MATCH_PARENT,
            MATCH_PARENT
    ));

    // Add views in order: empty view first, list view on top
    root.addView(emptyView);
    root.addView(listView);

    return root;
}