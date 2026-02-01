@Override
public Dimension minimumLayoutSize(Container parent) {
	int visibleAmount = Integer.MAX_VALUE;
	int scrollbarWidth = 0;
	Dimension dim = new Dimension();
	int index = 0;
	for (Component comp : parent.getComponents()) {
		if (!comp.isVisible()) {
			continue;
		}
		if (comp instanceof JScrollBar) {
			JScrollBar scrollBar = (JScrollBar) comp;
			visibleAmount = scrollBar.getVisibleAmount();
			Dimension sbPref = scrollBar.getPreferredSize();
			if (sbPref != null) {
				scrollbarWidth = sbPref.width;
			}
			continue;
		}
		// non-scrollbar component
		if (index % 2 == 0) {
			Dimension min = comp.getMinimumSize();
			if (min != null) {
				dim.width = Math.max(dim.width, min.width + C2WIDTH);
				dim.height += min.height;
			}
		}
		index++;
	}

	Insets insets = parent.getInsets();
	dim.width += scrollbarWidth + insets.left + insets.right;
	dim.height = Math.min(dim.height + insets.top + insets.bottom, visibleAmount);

	return dim;
}