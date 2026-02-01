@Override
public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
    // Reset renderer state to avoid stale visuals
    if (checkBox != null) {
        checkBox.setVisible(false);
        checkBox.setEnabled(false);
        checkBox.setSelected(false);
        checkBox.setOpaque(false);
        checkBox.setText("");
    }
    if (altLabel != null) {
        altLabel.setText("");
        altLabel.setVisible(true);
        altLabel.setEnabled(true);
    }

    if (value == null || !(value instanceof DefaultMutableTreeNode)) {
        return this;
    }

    DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
    Object userObject = node.getUserObject();
    String text = userObject != null ? userObject.toString() : "";
    altLabel.setText(text);

    // Apply Look & Feel–aware colors based on selection state
    java.awt.Color fg = selected ? UIManager.getColor("Tree.selectionForeground") : UIManager.getColor("Tree.textForeground");
    java.awt.Color bg = selected ? UIManager.getColor("Tree.selectionBackground") : UIManager.getColor("Tree.textBackground");
    if (fg != null) {
        altLabel.setForeground(fg);
    }
    if (bg != null) {
        altLabel.setBackground(bg);
    }

    // Lookup checking state directly from the map
    TreePath tp = new TreePath(node.getPath());
    CheckedNode cn = nodesCheckingState != null ? nodesCheckingState.get(tp) : null;
    if (cn == null) {
        // Node without checkbox support
        return this;
    }

    // Per-node checkbox visibility and enablement
    if (cn.isCheckBoxEnabled) {
        checkBox.setVisible(true);
        checkBox.setEnabled(true);
        checkBox.setSelected(cn.isSelected);
        // Partial-selection visual: use opaque to indicate mixed state
        checkBox.setOpaque(cn.isSelected && cn.hasChildren && !cn.allChildrenSelected);
    } else {
        checkBox.setVisible(false);
        checkBox.setEnabled(false);
    }

    return this;
}