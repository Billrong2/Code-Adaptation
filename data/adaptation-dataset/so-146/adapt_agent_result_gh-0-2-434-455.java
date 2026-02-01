private void enableDisableViewGroup(final android.view.ViewGroup root, final boolean enabled) {
	// Utility method to enable/disable interactivity across a view hierarchy
	if (root == null) {
		return;
	}

	final int childCount = root.getChildCount();
	for (int i = 0; i < childCount; i++) {
		android.view.View child = root.getChildAt(i);
		if (child == null) {
			continue;
		}

		// Toggle enabled state for focusable views
		if (child.isFocusable()) {
			child.setEnabled(enabled);
		}

		// Special handling for ListView
		if (child instanceof android.widget.ListView) {
			android.widget.ListView listView = (android.widget.ListView) child;
			if (listView.isFocusable()) {
				listView.setEnabled(enabled);
			}
			final int listChildCount = listView.getChildCount();
			for (int j = 0; j < listChildCount; j++) {
				android.view.View listChild = listView.getChildAt(j);
				if (listChild != null) {
					listChild.setEnabled(enabled);
				}
			}
		}

		// Recurse into nested ViewGroups
		if (child instanceof android.view.ViewGroup) {
			enableDisableViewGroup((android.view.ViewGroup) child, enabled);
		}
	}
}