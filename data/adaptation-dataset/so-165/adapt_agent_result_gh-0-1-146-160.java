@Override
public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
	// Guard against unexpected nulls
	if (value == null || !(value instanceof DefaultMutableTreeNode)) {
		return this;
	}
	if (nodesCheckingState == null) {
		return this;
	}
	DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
	Object obj = node.getUserObject();
	TreePath tp = new TreePath(node.getPath());
	// Direct lookup from nodesCheckingState instead of helper
	CheckedNode cn = nodesCheckingState.get(tp);
	if (cn == null) {
		return this;
	}
	checkBox.setSelected(cn.isSelected);
	checkBox.setText(obj != null ? obj.toString() : "");
	checkBox.setOpaque(cn.isSelected && cn.hasChildren && !cn.allChildrenSelected);
	return this;
}