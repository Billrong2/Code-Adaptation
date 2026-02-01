@Override public void layoutContainer(Container parent) {
	if (parent == null) {
		return;
	}

	Component[] components = parent.getComponents();
	if (components == null || components.length == 0) {
		return;
	}

	Insets insets = parent.getInsets();
	int containerWidth = Math.max(0, parent.getWidth());
	int containerHeight = Math.max(0, parent.getHeight());

	int x = insets.left;
	int y = insets.top;
	int availableWidth = Math.max(0, containerWidth - insets.left - insets.right);
	int availableHeight = Math.max(0, containerHeight - insets.top - insets.bottom);

	// Discover the first visible vertical scrollbar (if any)
	JScrollBar scrollBar = null;
	for (Component comp : components) {
		if (comp != null && comp.isVisible() && comp instanceof JScrollBar) {
			JScrollBar candidate = (JScrollBar) comp;
			if (candidate.getOrientation() == JScrollBar.VERTICAL) {
				scrollBar = candidate;
				break;
			}
		}
	}

	int scrollBarWidth = 0;
	int scrollOffset = 0;

	// Layout scrollbar flush right, full height
	if (scrollBar != null && scrollBar.isVisible()) {
		Dimension sbPref = scrollBar.getPreferredSize();
		scrollBarWidth = Math.max(0, sbPref != null ? sbPref.width : 0);
		int sbX = insets.left + Math.max(0, availableWidth - scrollBarWidth);
		scrollBar.setBounds(sbX, insets.top, scrollBarWidth, availableHeight);
		scrollOffset = Math.max(0, scrollBar.getValue());
	}

	int contentWidth = Math.max(0, availableWidth - scrollBarWidth);

	// Stack non-scrollbar components vertically, shifted by scroll offset
	int currentY = y - scrollOffset;
	for (Component comp : components) {
		if (comp == null || !comp.isVisible() || comp == scrollBar) {
			continue;
		}

		Dimension preferredSize = comp.getPreferredSize();
		int compHeight = preferredSize != null ? Math.max(0, preferredSize.height) : 0;
		comp.setBounds(x, currentY, contentWidth, compHeight);
		currentY += compHeight;
	}
}