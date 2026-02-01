protected JPanel getFontStylePanel() {
	if (fontStylePanel != null) {
		return fontStylePanel;
	}

	fontStylePanel = new JPanel(new BorderLayout());
	fontStylePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	fontStylePanel.setPreferredSize(new Dimension(140, TOP_PANE_PREFERRED_HEIGHT));

	// Label
	JTextField styleTextField = getFontStyleTextField();	// ensure non-null
	JLabel label = new JLabel("Font Style");
	label.setHorizontalAlignment(JLabel.LEFT);
	label.setHorizontalTextPosition(JLabel.LEFT);
	label.setDisplayedMnemonic('Y');
	if (styleTextField != null) {
		label.setLabelFor(styleTextField);
	}

	// Subpanel with text field and list
	JPanel innerPanel = new JPanel(new BorderLayout());
	if (styleTextField != null) {
		innerPanel.add(styleTextField, BorderLayout.NORTH);
	}

	JList styleList = getFontStyleList();	// ensure non-null
	if (styleList != null) {
		JScrollPane scrollPane = new JScrollPane(styleList);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		if (scrollPane.getVerticalScrollBar() != null) {
			scrollPane.getVerticalScrollBar().setFocusable(false);
		}
		innerPanel.add(scrollPane, BorderLayout.CENTER);
	}

	fontStylePanel.add(label, BorderLayout.NORTH);
	fontStylePanel.add(innerPanel, BorderLayout.CENTER);

	return fontStylePanel;
}