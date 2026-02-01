protected void updatePredecessorsAllChildrenSelectedState(TreePath tp) {
    if (tp == null || nodesCheckingState == null) {
        return;
    }
    TreePath parentPath = tp.getParentPath();
    if (parentPath == null) {
        return; // reached root
    }
    CheckedNode parentState = nodesCheckingState.get(parentPath);
    if (parentState == null) {
        return;
    }
    Object parentComp = parentPath.getLastPathComponent();
    if (!(parentComp instanceof DefaultMutableTreeNode)) {
        return;
    }
    DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parentComp;

    boolean prevAllChildrenSelected = parentState.allChildrenSelected;
    boolean prevIsSelected = parentState.isSelected;

    boolean allChildrenSelected = true;
    boolean anyChildSelected = false;

    for (int i = 0; i < parentNode.getChildCount(); i++) {
        TreePath childPath = parentPath.pathByAddingChild(parentNode.getChildAt(i));
        CheckedNode childState = nodesCheckingState.get(childPath);
        if (childState == null) {
            allChildrenSelected = false;
            continue;
        }
        if (!allSelected(childState)) {
            allChildrenSelected = false;
        }
        if (childState.isSelected) {
            anyChildSelected = true;
        }
    }

    parentState.allChildrenSelected = allChildrenSelected;
    parentState.isSelected = anyChildSelected;

    if (parentState.isSelected) {
        checkedPaths.add(parentPath);
    } else {
        checkedPaths.remove(parentPath);
    }

    // Stop recursion if no state changed to avoid unnecessary traversal
    if (prevAllChildrenSelected == parentState.allChildrenSelected
            && prevIsSelected == parentState.isSelected) {
        return;
    }

    updatePredecessorsAllChildrenSelectedState(parentPath);
}