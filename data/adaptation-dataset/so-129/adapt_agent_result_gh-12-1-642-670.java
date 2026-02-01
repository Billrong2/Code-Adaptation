protected JPanel getFontSizePanel() {
	// Lazy, thread-safe initialization
	if (fontSizePanel == null) {
		synchronized (this) {
			if (fontSizePanel == null) {
				// Defensive checks for required data/components
				if (this.fontSizeStrings == null) {
					this.fontSizeStrings = DEFAULT_FONT_SIZE_STRINGS;
				}
				final JTextField sizeField = getFontSizeTextField();
				final JList sizeList = getFontSizeList();

				fontSizePanel = new JPanel(new BorderLayout());
				fontSizePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

				// Accessible label
				JLabel label = new JLabel("Font Size");
				label.setHorizontalAlignment(JLabel.LEFT);
				label.setHorizontalTextPosition(JLabel.LEFT);
				label.setDisplayedMnemonic('S');
				label.setLabelFor(sizeField);

				// Center content: text field on top, list in scroll pane below
				JScrollPane scrollPane = new JScrollPane(sizeList);
				scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				scrollPane.getVerticalScrollBar().setFocusable(false);

				JPanel centerPanel = new JPanel(new BorderLayout());
				centerPanel.add(sizeField, BorderLayout.NORTH);
				centerPanel.add(scrollPane, BorderLayout.CENTER);

				fontSizePanel.add(label, BorderLayout.NORTH);
				fontSizePanel.add(centerPanel, BorderLayout.CENTER);

				// Preferred size derived from label width and constant height
				int preferredWidth = label.getPreferredSize().width + 80;
				fontSizePanel.setPreferredSize(new Dimension(preferredWidth, TOP_PANE_PREFERRED_HEIGHT));
			}
		}
	}
	return fontSizePanel;
}