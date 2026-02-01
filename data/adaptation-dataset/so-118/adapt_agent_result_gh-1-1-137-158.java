public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean selected, final boolean expanded, final boolean leaf, final int row, final boolean hasFocus) {
    // Default renderer setup
    if (checkBox == null) {
        return this;
    }

    // Resolve TreePath safely
    TreePath path = null;
    if (tree != null) {
        path = tree.getPathForRow(row);
    }

    // Determine text from user object
    String text = "";
    Object userObject = value;
    if (value instanceof DefaultMutableTreeNode) {
        userObject = ((DefaultMutableTreeNode) value).getUserObject();
    }
    if (userObject instanceof FEntry) {
        text = ((FEntry) userObject).getFileName();
    } else if (userObject != null) {
        text = userObject.toString();
    }
    checkBox.setText(text);

    // Apply checking state from backing model if available
    if (path != null && nodesCheckingState != null) {
        CheckedNode cn = nodesCheckingState.get(path);
        if (cn != null) {
            checkBox.setSelected(cn.isSelected);
            // Partial/branch selection visualization: non-opaque for partially selected parents
            boolean partiallySelected = cn.isSelected && cn.hasChildren && !cn.allChildrenSelected;
            checkBox.setOpaque(!partiallySelected);
        }
    }

    return this;
  }