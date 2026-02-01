public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
	// Reset renderer state to avoid leakage between rows
	checkBox.setSelected(false);
	checkBox.setText("");
	checkBox.setOpaque(false);
	this.setOpaque(false);

	if (value == null || !(value instanceof DefaultMutableTreeNode)) {
		return this;
	}

	DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
	Object userObject = node.getUserObject();
	if (userObject != null) {
		checkBox.setText(String.valueOf(userObject));
	}

	// Derive TreePath from the node
	TreePath treePath = null;
	TreeNode[] path = node.getPath();
	if (path != null) {
		treePath = new TreePath(path);
	}

	if (treePath == null || nodesCheckingState == null) {
		return this;
	}

	CheckedNode cn = nodesCheckingState.get(treePath);
	if (cn == null) {
		return this;
	}

	// Set checkbox selection state
	checkBox.setSelected(cn.isSelected);

	// Indicate partial selection: selected, has children, but not all children selected
	if (cn.isSelected && cn.hasChildren && !cn.allChildrenSelected) {
		checkBox.setOpaque(true);
	} else {
		checkBox.setOpaque(false);
	}

	return this;
}