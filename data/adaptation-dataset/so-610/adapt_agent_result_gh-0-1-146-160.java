@Override
public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
	// Preserve early return behavior on unexpected/null values
	if (!(value instanceof DefaultMutableTreeNode)) {
		return this;
	}
	final DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
	final Object userObject = node.getUserObject();
	if (userObject == null) {
		return this;
	}
	// Build TreePath for direct lookup
	final TreePath tp = new TreePath(node.getPath());
	// Safely access checking state map
	if (nodesCheckingState == null) {
		return this;
	}
	final CheckedNode cn = nodesCheckingState.get(tp);
	if (cn == null) {
		return this;
	}
	// Preserve original rendering logic
	checkBox.setSelected(cn.isSelected);
	checkBox.setText(userObject.toString());
	checkBox.setOpaque(cn.isSelected && cn.hasChildren && !cn.allChildrenSelected);
	return this;
}