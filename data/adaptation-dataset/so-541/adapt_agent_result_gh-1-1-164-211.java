@Override
protected void processMouseEvent(java.awt.event.MouseEvent e) {
	// Handle mouse clicks directly to manage check state; disable default selection and expand/collapse
	if (e == null) {
		return;
	}
	// Only react to left-button single clicks
	if (e.getID() == java.awt.event.MouseEvent.MOUSE_CLICKED && e.getButton() == java.awt.event.MouseEvent.BUTTON1 && e.getClickCount() == 1) {
		javax.swing.tree.TreeModel model = getModel();
		if (model == null || model.getRoot() == null) {
			return;
		}
		javax.swing.tree.TreePath path = getPathForLocation(e.getX(), e.getY());
		// Ignore clicks outside any tree row
		if (path != null && nodesCheckingState != null && nodesCheckingState.containsKey(path)) {
			toggleCheckState(path);
		}
		// Consume to prevent default JTree behavior (selection, expand/collapse)
		e.consume();
		return;
	}
	// For all other mouse events, do not delegate to super to fully disable selection changes
	e.consume();
}