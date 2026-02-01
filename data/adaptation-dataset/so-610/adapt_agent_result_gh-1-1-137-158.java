@Override
public Component getTreeCellRendererComponent(JTree tree, Object value,
        boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
    // Hardening: validate value type
    if (!(value instanceof DefaultMutableTreeNode)) {
        return this;
    }
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
    Object userObject = node.getUserObject();
    if (userObject == null) {
        return this;
    }

    // Build TreePath and lookup CheckedNode from nodesCheckingState
    TreePath tp = new TreePath(node.getPath());
    if (nodesCheckingState == null) {
        return this;
    }
    CheckedNode checkedNode = nodesCheckingState.get(tp);
    // Preserve early-return behavior when no CheckedNode is found
    if (checkedNode == null) {
        return this;
    }

    // Preserve selection and tri-state opacity logic
    checkBox.setSelected(checkedNode.isSelected);

    // Type-specific label customization
    String labelText;
    if (userObject instanceof FEntry) {
        labelText = ((FEntry) userObject).getFileName();
    } else {
        labelText = userObject.toString();
    }
    checkBox.setText(labelText);

    checkBox.setOpaque(checkedNode.isSelected && checkedNode.hasChildren && !checkedNode.allChildrenSelected);
    return this;
}