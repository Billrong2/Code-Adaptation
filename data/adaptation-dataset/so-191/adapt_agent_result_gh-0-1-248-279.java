private void updatePredecessorsWithCheckMode(javax.swing.tree.TreePath tp, boolean checkMode) {
        if (tp == null) {
            return;
        }
        javax.swing.tree.TreePath parentPath = tp.getParentPath();
        if (parentPath == null) {
            // reached root, stop propagation
            return;
        }
        CheckedNode parentCheckedNode = nodesCheckingState.get(parentPath);
        if (parentCheckedNode == null) {
            return;
        }
        javax.swing.tree.DefaultMutableTreeNode parentNode = (javax.swing.tree.DefaultMutableTreeNode) parentPath.getLastPathComponent();
        if (parentNode == null || parentNode.getChildCount() == 0) {
            parentCheckedNode.hasChildren = false;
            parentCheckedNode.allChildrenSelected = parentCheckedNode.isSelected;
        } else {
            boolean anyChildSelected = false;
            boolean allChildrenFullySelected = true;
            parentCheckedNode.hasChildren = true;
            for (int i = 0; i < parentNode.getChildCount(); i++) {
                javax.swing.tree.TreePath childPath = parentPath.pathByAddingChild(parentNode.getChildAt(i));
                CheckedNode childCheckedNode = nodesCheckingState.get(childPath);
                if (childCheckedNode == null) {
                    continue;
                }
                if (childCheckedNode.isSelected) {
                    anyChildSelected = true;
                }
                if (!allSelected(childCheckedNode)) {
                    allChildrenFullySelected = false;
                }
            }
            parentCheckedNode.isSelected = anyChildSelected;
            parentCheckedNode.allChildrenSelected = allChildrenFullySelected;
        }
        if (parentCheckedNode.isSelected) {
            checkedPaths.add(parentPath);
        } else {
            checkedPaths.remove(parentPath);
        }
        // propagate further up the tree
        updatePredecessorsWithCheckMode(parentPath, parentCheckedNode.isSelected);
    }