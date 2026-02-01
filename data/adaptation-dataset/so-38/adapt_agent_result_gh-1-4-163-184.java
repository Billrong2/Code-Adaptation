@Override public Dimension minimumLayoutSize(Container parent) {
	// Compute minimum layout size instead of preferred size
	if (parent == null) {
		return new Dimension();
	}

	int visibleAmount = Integer.MAX_VALUE;
	Dimension minDim = new Dimension();

	Component[] components = parent.getComponents();
	if (components != null && components.length > 0) {
		for (Component comp : components) {
			if (comp != null && comp.isVisible()) {
				if (comp instanceof JScrollBar) {
					JScrollBar scrollBar = (JScrollBar) comp;
					visibleAmount = scrollBar.getVisibleAmount();
				} else {
					Dimension min = comp.getMinimumSize();
					if (min != null) {
						minDim.width = Math.max(minDim.width, min.width);
						minDim.height += min.height;
					}
				}
			}
		}
	}

	Insets insets = parent.getInsets();
	if (insets != null) {
		minDim.height = Math.min(minDim.height + insets.top + insets.bottom, visibleAmount);
	}

	return minDim;
}