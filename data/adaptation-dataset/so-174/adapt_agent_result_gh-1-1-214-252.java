public void layoutContainer(Container parent) {
	if (parent == null) {
		return;
	}
	Component[] comps = parent.getComponents();
	if (comps == null) {
		return;
	}

	final int PAD_SMALL = 4;
	final int PAD_RIGHT = 12;
	final int SPACING = 4;

	Insets insets = parent.getInsets();
	int containerWidth = Math.max(0, parent.getWidth() - insets.left - insets.right);
	int containerHeight = Math.max(0, parent.getHeight() - insets.top - insets.bottom);

	// find visible scrollbar first
	JScrollBar scrollBar = null;
	for (Component c : comps) {
		if (c instanceof JScrollBar && c.isVisible()) {
			scrollBar = (JScrollBar) c;
			break;
		}
	}

	int scrollBarWidth = 0;
	int scrollOffset = 0;
	if (scrollBar != null) {
		Dimension sbPref = scrollBar.getPreferredSize();
		if (sbPref != null) {
			scrollBarWidth = Math.max(0, sbPref.width);
		}
		scrollOffset = Math.max(0, scrollBar.getValue());
		// dock scrollbar to the right
		scrollBar.setBounds(
				insets.left + Math.max(0, containerWidth - scrollBarWidth),
				insets.top,
				scrollBarWidth,
				containerHeight);
	}

	int contentWidth = Math.max(0, containerWidth - scrollBarWidth);

	// initial y position shifted by scroll offset
	int y = insets.top - scrollOffset + PAD_SMALL;
	int index = 0; // index among non-scrollbar components

	for (Component comp : comps) {
		if (comp == null || !comp.isVisible() || comp instanceof JScrollBar) {
			continue;
		}
		Dimension pref = comp.getPreferredSize();
		if (pref == null) {
			continue;
		}

		int w = Math.max(0, pref.width);
		int h = Math.max(0, pref.height);

		// odd/even pairing logic (1-based)
		boolean isOdd = (index % 2) == 0;
		if (isOdd) {
			// right-aligned item, advances y
			int x = insets.left + Math.max(0, contentWidth - w - PAD_RIGHT);
			comp.setBounds(x, y, Math.min(w, contentWidth), h);
			y += h + SPACING;
		} else {
			// paired item on same row, fixed offset using C2WIDTH
			int x = insets.left + Math.max(0, contentWidth - C2WIDTH);
			comp.setBounds(x, y - h - SPACING, Math.min(w, contentWidth), h);
		}
		index++;
	}
}