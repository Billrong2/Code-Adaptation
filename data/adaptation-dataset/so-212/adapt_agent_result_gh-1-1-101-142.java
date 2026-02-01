public void show(Component invoker, int x, int y) {
	final JScrollBar scrollBar = getScrollBar();
	final Container parent = this;

	// Only adjust popup size when scrollbar is visible
	if (scrollBar != null && scrollBar.isVisible() && parent != null) {
		final Component[] components = parent.getComponents();
		if (components != null && components.length > 0) {
			final Insets insets = parent.getInsets();
			int rowHeight = 0;
			int totalContentHeight = 0;
			int maxRowWidth = 0;
			int rowCount = 0;

			int index = 0;
			for (Component comp : components) {
				++index;
				if (!comp.isVisible() || comp instanceof JScrollBar) {
					continue;
				}
				// count every second non-scrollbar component as one row
				if (index % 2 == 0) {
					final Dimension pref = comp.getPreferredSize();
					if (rowHeight == 0) {
						rowHeight = pref.height;
					}
					maxRowWidth = Math.max(maxRowWidth, pref.width);
					totalContentHeight += pref.height;
					rowCount++;
				}
			}

			if (rowHeight > 0 && rowCount > 0) {
				final int visibleRows = Math.min(maximumVisibleRows, rowCount);
				final int visibleExtent = visibleRows * rowHeight;

				final int scrollbarWidth = scrollBar.getPreferredSize().width;
				final int popupWidth = maxRowWidth + C2WIDTH + scrollbarWidth + insets.left + insets.right;
				final int popupHeight = insets.top + insets.bottom + visibleExtent;

				// configure scrollbar
				scrollBar.setUnitIncrement(rowHeight);
				scrollBar.setBlockIncrement(visibleExtent);
				scrollBar.setValues(0, insets.top + insets.bottom + visibleExtent, 0,
						insets.top + insets.bottom + totalContentHeight);

				setPopupSize(new Dimension(popupWidth, popupHeight));
			}
		}
	}

	// Always show the popup after sizing
	super.show(invoker, x, y);
}