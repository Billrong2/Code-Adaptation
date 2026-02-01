private void updatePredecessorsWithCheckMode(TreePath tp, boolean checkMode) {
	if (tp == null || nodesCheckingState == null) {
		return;
	}
	TreePath parentPath = tp.getParentPath();
	// Stop recursion at root
	if (parentPath == null) {
		return;
	}
	CheckedNode parentState = nodesCheckingState.get(parentPath);
	if (parentState == null) {
		return;
	}
	Object parentComponent = parentPath.getLastPathComponent();
	if (!(parentComponent instanceof DefaultMutableTreeNode)) {
		return;
	}
	DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parentComponent;

	boolean anyChildSelected = false;
	boolean allChildrenFullySelected = true;

	for (int i = 0; i < parentNode.getChildCount(); i++) {
		Object child = parentNode.getChildAt(i);
		TreePath childPath = parentPath.pathByAddingChild(child);
		CheckedNode childState = nodesCheckingState.get(childPath);
		if (childState == null) {
			allChildrenFullySelected = false;
			continue;
		}
		if (childState.isSelected) {
			anyChildSelected = true;
		}
		// A child is fully selected only if it is selected and either has no children
		// or all of its children are fully selected
		boolean childFullySelected = childState.isSelected && (!childState.hasChildren || childState.allChildrenSelected);
		if (!childFullySelected) {
			allChildrenFullySelected = false;
		}
	}

	parentState.hasChildren = parentNode.getChildCount() > 0;
	parentState.isSelected = anyChildSelected;
	parentState.allChildrenSelected = anyChildSelected && allChildrenFullySelected;

	// Maintain checkedPaths consistency based on recomputed state
	if (parentState.isSelected) {
		checkedPaths.add(parentPath);
	} else {
		checkedPaths.remove(parentPath);
	}

	// Recurse upward
	updatePredecessorsWithCheckMode(parentPath, parentState.isSelected);
}