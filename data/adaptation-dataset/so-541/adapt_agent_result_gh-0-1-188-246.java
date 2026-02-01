public JCheckBoxTree() {
        super();
        // Disable default selection behavior; checking is handled internally
        setSelectionModel(new javax.swing.tree.DefaultTreeSelectionModel() {{
            setSelectionMode(javax.swing.tree.TreeSelectionModel.SINGLE_TREE_SELECTION);
            clearSelection();
        }});
        setToggleClickCount(0); // disable default double-click expand/toggle

        // Install custom renderer for checkbox display
        setCellRenderer(new CheckBoxCellRenderer());

        // Initialize checking state for an empty/default model
        resetCheckingState(getModel() != null ? (javax.swing.tree.DefaultMutableTreeNode) getModel().getRoot() : null);

        // Custom mouse handling for checkbox toggling
        final JCheckBoxTree checkBoxTree = this;
        addMouseListener(new java.awt.event.MouseListener() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                try {
                    if (e == null) {
                        return;
                    }
                    int row = checkBoxTree.getRowForLocation(e.getX(), e.getY());
                    if (row < 0) {
                        return;
                    }
                    javax.swing.tree.TreePath path = checkBoxTree.getPathForRow(row);
                    if (path == null) {
                        return;
                    }
                    CheckedNode cn = nodesCheckingState.get(path);
                    if (cn == null || !cn.isCheckBoxEnabled) {
                        return;
                    }
                    // Toggle selection state
                    boolean newState = !cn.isSelected;
                    // Propagate to descendants
                    checkSubTree(path, newState);
                    // Update ancestors
                    updatePredecessorsWithCheckMode(path, newState);
                    // Fire internal check-change event
                    fireCheckChangeEvent(new CheckChangeEvent(checkBoxTree));
                    // Repaint to reflect updated states
                    checkBoxTree.repaint();
                } catch (ClassCastException ex) {
                    // Ignore invalid node types to keep UI responsive
                } catch (RuntimeException ex) {
                    // Defensive: avoid breaking UI interaction on unexpected errors
                }
            }

            @Override public void mousePressed(java.awt.event.MouseEvent e) {}
            @Override public void mouseReleased(java.awt.event.MouseEvent e) {}
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {}
            @Override public void mouseExited(java.awt.event.MouseEvent e) {}
        });
    }