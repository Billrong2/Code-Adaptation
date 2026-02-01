protected JPanel getFontSizePanel() {
	if (fontSizePanel == null) {
		synchronized (this) {
			if (fontSizePanel == null) {
				// Constants for layout tuning
				final int PANEL_PADDING = 5;
				final int LABEL_HORIZONTAL_PADDING = 20;

				// Ensure required dependencies exist
				if (fontSizeStrings == null) {
					fontSizeStrings = DEFAULT_FONT_SIZE_STRINGS;
				}
				final JTextField sizeTextField = getFontSizeTextField();
				final JList sizeList = getFontSizeList();

				fontSizePanel = new JPanel(new BorderLayout());
				fontSizePanel.setBorder(BorderFactory.createEmptyBorder(PANEL_PADDING, PANEL_PADDING, PANEL_PADDING, PANEL_PADDING));

				// Top label
				JLabel label = new JLabel("Font Size");
				label.setHorizontalAlignment(JLabel.LEFT);
				label.setHorizontalTextPosition(JLabel.LEFT);
				label.setDisplayedMnemonic('S');
				label.setLabelFor(sizeTextField);
				fontSizePanel.add(label, BorderLayout.NORTH);

				// Body panel with text field on top and list below
				JPanel bodyPanel = new JPanel(new BorderLayout());
				bodyPanel.add(sizeTextField, BorderLayout.NORTH);

				JScrollPane scrollPane = new JScrollPane(sizeList);
				scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				scrollPane.getVerticalScrollBar().setFocusable(false);
				scrollPane.setFocusable(false);
				bodyPanel.add(scrollPane, BorderLayout.CENTER);

				fontSizePanel.add(bodyPanel, BorderLayout.CENTER);

				// Compute preferred size based on label width plus padding and fixed height
				int preferredWidth = label.getPreferredSize().width + LABEL_HORIZONTAL_PADDING;
				fontSizePanel.setPreferredSize(new Dimension(preferredWidth, TOP_PANE_PREFERRED_HEIGHT));
			}
		}
	}
	return fontSizePanel;
}