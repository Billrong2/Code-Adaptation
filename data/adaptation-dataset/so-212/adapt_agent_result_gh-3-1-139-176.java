public void show(Component invoker, int x, int y) {
	// Compute popup size at show-time based on non-scrollbar children
	Container parent = this;
	if (parent == null) {
		super.show(invoker, x, y);
		return;
	}

	Component[] components = parent.getComponents();
	if (components == null || components.length == 0) {
		super.show(invoker, x, y);
		return;
	}

	Insets insets = parent.getInsets();
	if (insets == null) {
		insets = new Insets(0, 0, 0, 0);
	}

	int maxChildWidth = 0;
	int totalContentHeight = 0;
	int visibleContentHeight = 0;
	int firstItemHeight = 0;
	int rowsCounted = 0;

	for (Component comp : components) {
		if (comp == null || comp instanceof JScrollBar) {
			continue;
		}
		Dimension childPrefSize = comp.getPreferredSize();
		if (childPrefSize == null) {
			continue;
		}
		maxChildWidth = Math.max(maxChildWidth, Math.max(0, childPrefSize.width));
		totalContentHeight += Math.max(0, childPrefSize.height);

		if (rowsCounted < maximumVisibleRows) {
			visibleContentHeight += Math.max(0, childPrefSize.height);
			rowsCounted++;
			if (firstItemHeight == 0) {
				firstItemHeight = Math.max(0, childPrefSize.height);
			}
		}
	}

	// Include insets
	visibleContentHeight += insets.top + insets.bottom;
	totalContentHeight += insets.top + insets.bottom;

	visibleContentHeight = Math.max(0, visibleContentHeight);
	totalContentHeight = Math.max(0, totalContentHeight);

	JScrollBar scrollBar = getScrollBar();
	int scrollBarWidth = 0;
	boolean needsScrollBar = rowsCounted >= maximumVisibleRows && totalContentHeight > visibleContentHeight;
	if (scrollBar != null && needsScrollBar) {
		Dimension sbPref = scrollBar.getPreferredSize();
		if (sbPref != null) {
			scrollBarWidth = Math.max(0, sbPref.width);
		}
	}

	int popupWidth = maxChildWidth + scrollBarWidth + insets.left + insets.right;
	int popupHeight = visibleContentHeight;

	popupWidth = Math.max(0, popupWidth);
	popupHeight = Math.max(0, popupHeight);

	// Configure scrollbar dynamically if visible
	if (scrollBar != null) {
		scrollBar.setVisible(needsScrollBar);
		if (needsScrollBar) {
			int unitIncrement = firstItemHeight > 0 ? firstItemHeight : 1;
			int blockIncrement = visibleContentHeight > 0 ? visibleContentHeight : unitIncrement;
			scrollBar.setUnitIncrement(unitIncrement);
			scrollBar.setBlockIncrement(blockIncrement);
			scrollBar.setValues(scrollBar.getValue(), visibleContentHeight, 0, totalContentHeight);
			scrollBar.setBounds(0, 0, scrollBarWidth, popupHeight);
		} else {
			scrollBar.setValue(0);
		}
	}

	// Apply computed size and show
	setPopupSize(new Dimension(popupWidth, popupHeight));
	super.show(invoker, x, y);
}