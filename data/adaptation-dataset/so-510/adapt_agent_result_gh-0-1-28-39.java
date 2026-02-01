static final void addListenerDeeply(final javafx.scene.Node node, final javafx.event.EventHandler<javafx.scene.input.MouseEvent> handler) {
	// Null-safety: nothing to do if inputs are missing
	if (node == null || handler == null) {
		return;
	}

	// Guard against detached nodes where event registration may be unsafe
	if (node.getScene() == null) {
		return;
	}

	// Register only the required mouse events on this node
	node.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_MOVED, handler);
	node.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED, handler);
	node.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_DRAGGED, handler);

	// Recurse into children if this node is a Parent
	if (node instanceof javafx.scene.Parent) {
		final javafx.scene.Parent parent = (javafx.scene.Parent) node;
		final javafx.collections.ObservableList<javafx.scene.Node> children = parent.getChildrenUnmodifiable();
		for (final javafx.scene.Node child : children) {
			ResizeAndMoveHelper.addListenerDeeply(child, handler);
		}
	}
}