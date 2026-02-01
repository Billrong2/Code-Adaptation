public static void addResizeListener(final javafx.stage.Stage stage, final javafx.scene.layout.BorderPane menu) {
	// hardening: validate inputs
	if (stage == null || menu == null) {
		return;
	}
	final javafx.scene.Scene scene = stage.getScene();
	if (scene == null) {
		return;
	}

	// construct listener with new (Stage, BorderPane) signature
	final ResizeListener resizeListener = new ResizeListener(stage, menu);

	// register only required mouse events on the scene
	scene.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_MOVED, resizeListener);
	scene.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED, resizeListener);
	scene.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_DRAGGED, resizeListener);

	// delegate deep attachment to helper
	final javafx.scene.Parent root = scene.getRoot();
	if (root != null) {
		ResizeAndMoveHelper.addListenerDeeply(root, resizeListener);
	}
  }