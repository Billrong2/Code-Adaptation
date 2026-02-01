@Override
public Component getTreeCellRendererComponent(JTree tree, Object value,
        boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
    // Basic hardening against unexpected values
    if (!(value instanceof DefaultMutableTreeNode)) {
        return this;
    }
    final DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
    final Object userObject = node.getUserObject();

    final TreePath tp = new TreePath(node.getPath());
    // Replace helper lookup with map-based retrieval
    final CheckedNode cn = (nodesCheckingState != null) ? nodesCheckingState.get(tp) : null;
    // Preserve existing early-return behavior
    if (cn == null) {
        return this;
    }

    checkBox.setSelected(cn.isSelected);

    // Customize label text rendering for FEntry
    String labelText;
    if (userObject instanceof de.mas.jnustool.FEntry) {
        labelText = ((de.mas.jnustool.FEntry) userObject).getFileName();
    } else if (userObject != null) {
        labelText = userObject.toString();
    } else {
        labelText = "";
    }
    checkBox.setText(labelText);

    // Keep existing opaque logic unchanged
    checkBox.setOpaque(cn.isSelected && cn.hasChildren && !cn.allChildrenSelected);
    return this;
}