@Override
public Dimension preferredLayoutSize(Container parent) {
	// Two-column row assumption: only every second visible non-scrollbar component is measured
	if (parent == null || parent.getComponents() == null) {
		return new Dimension(0, 0);
	}

	int maxVisibleHeight = Integer.MAX_VALUE;
	int componentIndex = 0;
	int scrollbarPreferredWidth = 0;
	Dimension totalDim = new Dimension(0, 0);

	for (Component comp : parent.getComponents()) {
		if (comp == null || !comp.isVisible()) {
			continue;
		}

		if (comp instanceof JScrollBar) {
			JScrollBar scrollBar = (JScrollBar) comp;
			// cap height by scrollbar visible amount
			maxVisibleHeight = scrollBar.getVisibleAmount();
			// remember scrollbar width to add later
			Dimension sbPref = scrollBar.getPreferredSize();
			if (sbPref != null) {
				scrollbarPreferredWidth = Math.max(scrollbarPreferredWidth, sbPref.width);
			}
			continue;
		}

		// count only non-scrollbar components
		componentIndex++;
		if ((componentIndex % 2) == 0) {
			Dimension pref = comp.getPreferredSize();
			if (pref != null) {
				totalDim.height += pref.height;
				totalDim.width = Math.max(totalDim.width, pref.width + C2WIDTH);
			}
		}
	}

	Insets insets = parent.getInsets();
	int heightWithInsets = totalDim.height + (insets != null ? insets.top + insets.bottom : 0);
	// height is capped by scrollbar visible amount; width is not affected by insets
	totalDim.height = Math.min(heightWithInsets, maxVisibleHeight);
	totalDim.width += scrollbarPreferredWidth;

	return totalDim;
}