protected JPanel getFontSizePanel() {
	// Ensure construction happens on the EDT
	if (!SwingUtilities.isEventDispatchThread()) {
		final JPanel[] result = new JPanel[1];
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					result[0] = getFontSizePanel();
				}
			});
		} catch (Exception e) {
			// Fallback: return an empty panel if EDT handoff fails
			return new JPanel();
		}
		return result[0];
	}

	if (fontSizePanel == null) {
		synchronized (this) {
			if (fontSizePanel == null) {
				// Defensive checks
				if (fontSizeStrings == null) {
					fontSizeStrings = DEFAULT_FONT_SIZE_STRINGS;
				}

				fontSizePanel = new JPanel(new BorderLayout());
				fontSizePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

				// Inner panel with text field and scrollable list
				JPanel innerPanel = new JPanel(new BorderLayout());

				JTextField sizeField = getFontSizeTextField();
				if (sizeField == null) {
					sizeField = new JTextField();
				}
				innerPanel.add(sizeField, BorderLayout.NORTH);

				JList sizeList = getFontSizeList();
				if (sizeList == null) {
					sizeList = new JList(fontSizeStrings);
				}

				JScrollPane scrollPane = new JScrollPane(sizeList);
				scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				JScrollBar vBar = scrollPane.getVerticalScrollBar();
				if (vBar != null) {
					vBar.setFocusable(false);
				}
				innerPanel.add(scrollPane, BorderLayout.CENTER);

				// Accessible label
				JLabel label = new JLabel("Font Size");
				label.setHorizontalAlignment(JLabel.LEFT);
				label.setHorizontalTextPosition(JLabel.LEFT);
				label.setDisplayedMnemonic('S');
				label.setLabelFor(sizeField);

				fontSizePanel.add(label, BorderLayout.NORTH);
				fontSizePanel.add(innerPanel, BorderLayout.CENTER);

				// Preferred size based on label width plus padding
				Dimension labelSize = label.getPreferredSize();
				int preferredWidth = (labelSize != null ? labelSize.width : 0) + 20;
				fontSizePanel.setPreferredSize(new Dimension(preferredWidth, TOP_PANE_PREFERRED_HEIGHT));
			}
		}
	}
	return fontSizePanel;
}