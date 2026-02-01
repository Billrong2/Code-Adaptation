private void updatePredecessorsWithCheckMode(final TreePath tp, final boolean checkMode) {
	// Propagate check state changes upward based on children states (ignore checkMode)
	if (tp == null) {
		return;
	}
	TreePath parentPath = tp.getParentPath();
	while (parentPath != null) {
		CheckedNode parentState = nodesCheckingState.get(parentPath);
		if (parentState == null) {
			return;
		}
		Object parentComponent = parentPath.getLastPathComponent();
		if (!(parentComponent instanceof DefaultMutableTreeNode)) {
			return;
		}
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parentComponent;
		int childCount = parentNode.getChildCount();
		if (childCount <= 0) {
			// No children: nothing to aggregate, stop propagation
			return;
		}
		boolean anyChildSelected = false;
		boolean allChildrenSelected = true;
		for (int i = 0; i < childCount; i++) {
			Object child = parentNode.getChildAt(i);
			TreePath childPath = parentPath.pathByAddingChild(child);
			CheckedNode childState = nodesCheckingState.get(childPath);
			if (childState == null) {
				allChildrenSelected = false;
				continue;
			}
			if (childState.isSelected) {
				anyChildSelected = true;
			}
			if (!childState.isSelected || !childState.allChildrenSelected) {
				allChildrenSelected = false;
			}
		}
		parentState.isSelected = anyChildSelected;
		parentState.allChildrenSelected = allChildrenSelected;
		if (parentState.isSelected) {
			checkedPaths.add(parentPath);
		} else {
			checkedPaths.remove(parentPath);
		}
		parentPath = parentPath.getParentPath();
	}
}