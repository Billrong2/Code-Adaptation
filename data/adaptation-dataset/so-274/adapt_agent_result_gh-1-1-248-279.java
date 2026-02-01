private void updatePredecessorsWithCheckMode(TreePath tp, boolean checkMode) {
        if (tp == null || nodesCheckingState == null) {
            return;
        }
        TreePath parentPath = tp.getParentPath();
        while (parentPath != null) {
            CheckedNode parentCheckedNode = nodesCheckingState.get(parentPath);
            if (parentCheckedNode == null) {
                return;
            }
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parentPath.getLastPathComponent();
            boolean allChildrenSelected = true;
            boolean anyChildSelected = false;
            for (int i = 0; i < parentNode.getChildCount(); i++) {
                TreePath childPath = parentPath.pathByAddingChild(parentNode.getChildAt(i));
                CheckedNode childCheckedNode = nodesCheckingState.get(childPath);
                if (childCheckedNode == null) {
                    allChildrenSelected = false;
                    continue;
                }
                if (!allSelected(childCheckedNode)) {
                    allChildrenSelected = false;
                }
                if (childCheckedNode.isSelected) {
                    anyChildSelected = true;
                }
            }
            parentCheckedNode.allChildrenSelected = allChildrenSelected;
            parentCheckedNode.isSelected = anyChildSelected;
            if (parentCheckedNode.isSelected) {
                checkedPaths.add(parentPath);
            } else {
                checkedPaths.remove(parentPath);
            }
            parentPath = parentPath.getParentPath();
        }
    }