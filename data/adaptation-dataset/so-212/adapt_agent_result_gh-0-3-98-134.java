@Override
public void show(java.awt.Component invoker, int x, int y) {
	// Move sizing logic here: compute content size and configure scrollbar before showing
	if (invoker == null) {
		return;
	}

	javax.swing.JScrollBar scrollBar = getScrollBar();
	if (scrollBar == null) {
		super.show(invoker, x, y);
		return;
	}

	java.awt.Insets insets = getInsets();
	int maxPreferredWidth = 0;
	int totalContentHeight = 0;
	int rowHeight = 0;
	int rowCount = 0;

	for (java.awt.Component comp : getComponents()) {
		if (!comp.isVisible() || comp instanceof javax.swing.JScrollBar) {
			continue;
		}
		java.awt.Dimension pref = comp.getPreferredSize();
		maxPreferredWidth = Math.max(maxPreferredWidth, pref.width);
		totalContentHeight += pref.height;
		if (rowHeight <= 0) {
			rowHeight = pref.height;
		}
		rowCount++;
	}

	// Guard against empty or zero-row scenarios
	if (rowCount <= 0 || rowHeight <= 0) {
		scrollBar.setVisible(false);
		super.show(invoker, x, y);
		return;
	}

	int maxRows = maximumVisibleRows > 0 ? maximumVisibleRows : rowCount;
	int visibleRows = Math.min(rowCount, maxRows);
	int visibleExtent = visibleRows * rowHeight;

	// Configure scrollbar based on content
	boolean needsScroll = totalContentHeight > visibleExtent;
	scrollBar.setVisible(needsScroll);
	if (needsScroll) {
		scrollBar.setUnitIncrement(rowHeight);
		scrollBar.setBlockIncrement(visibleExtent);
		int extentWithInsets = visibleExtent + insets.top + insets.bottom;
		int maxWithInsets = totalContentHeight + insets.top + insets.bottom;
		scrollBar.setValues(scrollBar.getValue(), extentWithInsets, 0, maxWithInsets);
	}

	// Compute final popup size including insets and scrollbar width
	int width = maxPreferredWidth + insets.left + insets.right;
	if (needsScroll) {
		java.awt.Dimension sbPref = scrollBar.getPreferredSize();
		width += sbPref.width;
	}
	int height = (needsScroll ? visibleExtent : totalContentHeight) + insets.top + insets.bottom;

	setPopupSize(new java.awt.Dimension(width, height));
	super.show(invoker, x, y);
}