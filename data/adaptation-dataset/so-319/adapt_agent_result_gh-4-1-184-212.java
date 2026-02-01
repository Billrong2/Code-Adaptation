@Override
public Dimension minimumLayoutSize(Container parent) {
	if (parent == null) {
		return new Dimension();
	}

	int visibleAmount = Integer.MAX_VALUE;
	Dimension minDim = new Dimension();
	int scrollbarWidth = 0;

	Component[] components = parent.getComponents();
	if (components == null) {
		return minDim;
	}

	int visibleNonScrollbarIndex = 0;
	for (Component comp : components) {
		if (comp == null || !comp.isVisible()) {
			continue;
		}

		if (comp instanceof JScrollBar) {
			JScrollBar scrollBar = (JScrollBar) comp;
			visibleAmount = scrollBar.getVisibleAmount();
			Dimension sbMin = scrollBar.getMinimumSize();
			if (sbMin != null) {
				scrollbarWidth = sbMin.width;
			}
		} else {
			// count only visible, non-scrollbar components
			visibleNonScrollbarIndex++;
			// process every second (even-indexed) component
			if (visibleNonScrollbarIndex % 2 == 0) {
				Dimension min = comp.getMinimumSize();
				if (min != null) {
					minDim.width = Math.max(minDim.width, min.width + C2WIDTH);
					minDim.height += min.height;
				}
			}
		}
	}

	// width is not affected by insets; only add scrollbar width
	minDim.width += scrollbarWidth;

	Insets insets = parent.getInsets();
	if (insets != null) {
		minDim.height = Math.min(minDim.height + insets.top + insets.bottom, visibleAmount);
	}

	return minDim;
}