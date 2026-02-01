@Override
public void handle(final MouseEvent event) {
	if (this.stage == null) {
		return;
	}
	final javafx.scene.Scene scene = this.stage.getScene();
	if (scene == null) {
		return;
	}

	final double borderSize = this.border;
	final double minWidth = Math.max(this.stage.getMinWidth(), borderSize * 2);
	final double minHeight = Math.max(this.stage.getMinHeight(), borderSize * 2);

	final EventType<? extends MouseEvent> type = event.getEventType();

	if (type == MouseEvent.MOUSE_MOVED) {
		final double x = event.getSceneX();
		final double y = event.getSceneY();
		final double width = this.stage.getWidth();
		final double height = this.stage.getHeight();

		Cursor cursor = Cursor.DEFAULT;

		final boolean left = x >= 0 && x <= borderSize;
		final boolean right = x <= width && x >= width - borderSize;
		final boolean top = y >= 0 && y <= borderSize;
		final boolean bottom = y <= height && y >= height - borderSize;

		if (left && top) {
			cursor = Cursor.NW_RESIZE;
		} else if (right && top) {
			cursor = Cursor.NE_RESIZE;
		} else if (left && bottom) {
			cursor = Cursor.SW_RESIZE;
		} else if (right && bottom) {
			cursor = Cursor.SE_RESIZE;
		} else if (left) {
			cursor = Cursor.W_RESIZE;
		} else if (right) {
			cursor = Cursor.E_RESIZE;
		} else if (top) {
			cursor = Cursor.N_RESIZE;
		} else if (bottom) {
			cursor = Cursor.S_RESIZE;
		}

		this.cursorEvent = cursor;
		scene.setCursor(cursor);
		this.resizing = cursor != Cursor.DEFAULT;
		return;
	}

	if (type == MouseEvent.MOUSE_PRESSED) {
		this.startX = this.stage.getWidth() - event.getSceneX();
		this.startY = this.stage.getHeight() - event.getSceneY();
		return;
	}

	if (type == MouseEvent.MOUSE_DRAGGED && this.resizing && this.cursorEvent != Cursor.DEFAULT) {
		final double mouseScreenX = event.getScreenX();
		final double mouseScreenY = event.getScreenY();
		final double stageX = this.stage.getX();
		final double stageY = this.stage.getY();

		if (this.cursorEvent == Cursor.W_RESIZE || this.cursorEvent == Cursor.NW_RESIZE || this.cursorEvent == Cursor.SW_RESIZE) {
			double newWidth = (stageX + this.stage.getWidth()) - mouseScreenX;
			if (newWidth >= minWidth) {
				this.stage.setX(mouseScreenX);
				this.stage.setWidth(newWidth);
			}
		}

		if (this.cursorEvent == Cursor.E_RESIZE || this.cursorEvent == Cursor.NE_RESIZE || this.cursorEvent == Cursor.SE_RESIZE) {
			double newWidth = event.getSceneX() + this.startX;
			if (newWidth >= minWidth) {
				this.stage.setWidth(newWidth);
			}
		}

		if (this.cursorEvent == Cursor.N_RESIZE || this.cursorEvent == Cursor.NW_RESIZE || this.cursorEvent == Cursor.NE_RESIZE) {
			double newHeight = (stageY + this.stage.getHeight()) - mouseScreenY;
			if (newHeight >= minHeight) {
				this.stage.setY(mouseScreenY);
				this.stage.setHeight(newHeight);
			}
		}

		if (this.cursorEvent == Cursor.S_RESIZE || this.cursorEvent == Cursor.SW_RESIZE || this.cursorEvent == Cursor.SE_RESIZE) {
			double newHeight = event.getSceneY() + this.startY;
			if (newHeight >= minHeight) {
				this.stage.setHeight(newHeight);
			}
		}
	}
}