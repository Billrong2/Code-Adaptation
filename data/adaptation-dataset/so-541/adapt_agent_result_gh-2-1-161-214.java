public JCheckBoxTree(final NUSTitle rootTitle) {
    super();
    // Disable default selection behavior; we manage state ourselves
    setSelectionModel(new DefaultTreeSelectionModel() {
        private static final long serialVersionUID = 1L;
        @Override public void setSelectionPath(TreePath path) {}
        @Override public void addSelectionPath(TreePath path) {}
    });

    // Build tree model from external domain data
    DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(rootTitle);
    if (rootTitle != null && rootTitle.getEntries() != null) {
        for (FEntry entry : rootTitle.getEntries()) {
            if (entry == null) continue;
            DefaultMutableTreeNode entryNode = new DefaultMutableTreeNode(entry);
            rootNode.add(entryNode);
            if (entry.getChildren() != null) {
                for (FEntry child : entry.getChildren()) {
                    if (child != null) {
                        entryNode.add(new DefaultMutableTreeNode(child));
                    }
                }
            }
        }
    }
    setModel(new DefaultTreeModel(rootNode));

    // Custom renderer for checkbox visuals
    setCellRenderer(new CheckBoxCellRenderer());

    // Initialize internal checking state
    resetCheckingState();

    // Custom mouse handler for toggling check state
    final MouseListener mouseHandler = new MouseListener() {
        @Override public void mouseClicked(MouseEvent e) {
            int row = getRowForLocation(e.getX(), e.getY());
            if (row < 0) return;
            TreePath path = getPathForRow(row);
            if (path == null) return;
            CheckedNode cn = nodesCheckingState.get(path);
            if (cn == null) return;

            boolean newState = !cn.isSelected;
            // Apply to subtree and update ancestors
            checkSubTree(path, newState);
            updatePredecessorsWithCheckMode(path, newState);

            // Fire event only if state actually changed
            fireCheckChangeEvent(new CheckChangeEvent(JCheckBoxTree.this));
            repaint();
        }
        @Override public void mousePressed(MouseEvent e) {}
        @Override public void mouseReleased(MouseEvent e) {}
        @Override public void mouseEntered(MouseEvent e) {}
        @Override public void mouseExited(MouseEvent e) {}
    };
    addMouseListener(mouseHandler);
}
