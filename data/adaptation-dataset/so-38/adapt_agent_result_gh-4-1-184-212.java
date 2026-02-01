@Override
public Dimension minimumLayoutSize(Container parent) {
	// Compute minimum layout size considering only even-indexed visible non-scrollbar components
	if (parent == null) {
		return new Dimension();
	}
	Component[] components = parent.getComponents();
	if (components == null) {
		return new Dimension();
	}

	int visibleAmount = Integer.MAX_VALUE;
	Dimension resultDim = new Dimension();
	int index = 0;
	int scrollBarWidth = 0;

	for (final Component comp : components) {
		++index;
		if (!comp.isVisible()) {
			continue;
		}
		if (comp instanceof JScrollBar) {
			JScrollBar scrollBar = (JScrollBar) comp;
			visibleAmount = scrollBar.getVisibleAmount();
			Dimension sbMin = scrollBar.getMinimumSize();
			if (sbMin != null) {
				scrollBarWidth = sbMin.width;
			}
		} else if (index % 2 == 0) {
			// process only even-indexed components (skips first and all odd indices)
			Dimension min = comp.getMinimumSize();
			if (min != null) {
				resultDim.width = Math.max(resultDim.width, min.width + C2WIDTH);
				resultDim.height += min.height;
			}
		}
	}

	// include scrollbar width if present
	resultDim.width += scrollBarWidth;

	Insets insets = parent.getInsets();
	int insetsHeight = (insets != null) ? (insets.top + insets.bottom) : 0;

	// cap height by visible amount if a scrollbar was found
	if (visibleAmount != Integer.MAX_VALUE) {
		resultDim.height = Math.min(resultDim.height + insetsHeight, visibleAmount);
	} else {
		resultDim.height += insetsHeight;
	}

	return resultDim;
}