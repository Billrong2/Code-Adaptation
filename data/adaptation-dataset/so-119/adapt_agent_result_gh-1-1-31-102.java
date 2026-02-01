public DraggableTabbedPane() {
	super();

	addMouseMotionListener(new MouseMotionAdapter() {
		@Override
		public void mouseDragged(MouseEvent e) {
			if (e == null) {
				return;
			}

			// Start dragging
			if (!dragging) {
				if (getUI() == null) {
					return;
				}
				int tabNumber = getUI().tabForCoordinate(DraggableTabbedPane.this, e.getX(), e.getY());
				if (tabNumber < 0 || tabNumber >= getTabCount()) {
					return;
				}

				draggedTabIndex = tabNumber;
				Rectangle bounds = getUI().getTabBounds(DraggableTabbedPane.this, tabNumber);
				if (bounds == null || bounds.width <= 0 || bounds.height <= 0) {
					return;
				}

				// Paint the full tabbed pane into an offscreen image
				BufferedImage totalImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
				Graphics totalGraphics = totalImage.getGraphics();
				boolean wasDoubleBuffered = isDoubleBuffered();
				try {
					totalGraphics.setClip(bounds);
					setDoubleBuffered(false);
					// Use full paint(), not paintComponent(), to correctly render UI
					paint(totalGraphics);
				} finally {
					setDoubleBuffered(wasDoubleBuffered);
					totalGraphics.dispose();
				}

				// Extract just the dragged tab image
				tabImage = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
				Graphics tabGraphics = tabImage.getGraphics();
				try {
					tabGraphics.drawImage(totalImage,
						0, 0, bounds.width, bounds.height,
						bounds.x, bounds.y, bounds.x + bounds.width, bounds.y + bounds.height,
						DraggableTabbedPane.this);
				} finally {
					tabGraphics.dispose();
				}

				dragging = true;
				currentMouseLocation = e.getPoint();
				repaint();
			} else {
				// Continue dragging
				currentMouseLocation = e.getPoint();
				repaint();
			}
			super.mouseDragged(e);
		}
	});

	addMouseListener(new MouseAdapter() {
		@Override
		public void mouseReleased(MouseEvent e) {
			if (!dragging || e == null) {
				dragging = false;
				tabImage = null;
				return;
			}

			int targetIndex = -1;
			if (getUI() != null && getTabCount() > 0) {
				// Try to resolve tab under cursor
				targetIndex = getUI().tabForCoordinate(DraggableTabbedPane.this, e.getX(), e.getY());

				// Snap left of first tab to index 0
				Rectangle firstBounds = getUI().getTabBounds(DraggableTabbedPane.this, 0);
				if (firstBounds != null && e.getX() < firstBounds.x) {
					targetIndex = 0;
				}

				// Snap beyond last tab to last index
				Rectangle lastBounds = getUI().getTabBounds(DraggableTabbedPane.this, getTabCount() - 1);
				if (lastBounds != null && e.getX() > lastBounds.x + lastBounds.width) {
					targetIndex = getTabCount() - 1;
				}
			}

			// Perform reordering if indices are valid
			if (targetIndex >= 0 && targetIndex < getTabCount()
					&& draggedTabIndex >= 0 && draggedTabIndex < getTabCount()
					&& targetIndex != draggedTabIndex) {

				Component tabComponent = getComponentAt(draggedTabIndex);
				Component tabHeader = getTabComponentAt(draggedTabIndex);
				String title = getTitleAt(draggedTabIndex);
				removeTabAt(draggedTabIndex);

				// Adjust target index if necessary after removal
				if (targetIndex > draggedTabIndex) {
					targetIndex--;
				}

				insertTab(title, null, tabComponent, null, targetIndex);
				if (tabHeader != null) {
					setTabComponentAt(targetIndex, tabHeader);
				}

				// Select the moved tab
				setSelectedIndex(targetIndex);
			}

			dragging = false;
			tabImage = null;
			currentMouseLocation = null;
		}
	});
}