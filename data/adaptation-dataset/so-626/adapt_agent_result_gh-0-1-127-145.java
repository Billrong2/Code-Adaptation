@Override
protected void processMouseEvent(MouseEvent e, JLayer<? extends JTabbedPane> layer) {
	// React only to mouse-click events
	if (e == null || layer == null || e.getID() != MouseEvent.MOUSE_CLICKED) {
		return;
	}
	final JTabbedPane tabbedPane = layer.getView();
	if (tabbedPane == null) {
		return;
	}

	final Point clickPoint = e.getPoint();
	if (clickPoint == null) {
		return;
	}

	final int tabIndex = tabbedPane.indexAtLocation(clickPoint.x, clickPoint.y);
	if (tabIndex < 0 || tabIndex >= tabbedPane.getTabCount()) {
		return;
	}

	final Rectangle tabBounds = tabbedPane.getBoundsAt(tabIndex);
	if (tabBounds == null) {
		return;
	}

	final Dimension pref = button.getPreferredSize();
	if (pref == null || pref.width <= 0 || pref.height <= 0) {
		return;
	}

	// Compute close-button hit area for the clicked tab
	final int bx = tabBounds.x + tabBounds.width - pref.width - 2;
	final int by = tabBounds.y + (tabBounds.height - pref.height) / 2;
	final Rectangle closeRect = new Rectangle(bx, by, pref.width, pref.height);

	if (closeRect.contains(clickPoint)) {
		// Remove the clicked tab when the close area is hit
		tabbedPane.removeTabAt(tabIndex);
	}

	// Request repaint safely on the EDT
	if (javax.swing.SwingUtilities.isEventDispatchThread()) {
		layer.repaint();
	} else {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override public void run() {
				layer.repaint();
			}
		});
	}
}