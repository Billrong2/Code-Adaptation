protected JPanel getFontStylePanel() {
	if (fontStylePanel == null) {
		// Lazily initialize and cache the font style panel
		fontStylePanel = new JPanel();
		fontStylePanel.setLayout(new BorderLayout());
		fontStylePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		fontStylePanel.setPreferredSize(new Dimension(140, TOP_PANE_PREFERRED_HEIGHT));

		// Ensure dependent components are initialized
		JTextField styleTextField = getFontStyleTextField();
		JList styleList = getFontStyleList();

		// Scrollable list with always-visible vertical scrollbar
		JScrollPane scrollPane = new JScrollPane(styleList);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.getVerticalScrollBar().setFocusable(false);

		// Inner panel holding text field and list
		JPanel innerPanel = new JPanel(new BorderLayout());
		innerPanel.add(styleTextField, BorderLayout.NORTH);
		innerPanel.add(scrollPane, BorderLayout.CENTER);

		// Accessible label
		JLabel label = new JLabel("Font Style");
		label.setHorizontalAlignment(JLabel.LEFT);
		label.setHorizontalTextPosition(JLabel.LEFT);
		label.setDisplayedMnemonic('Y');
		label.setLabelFor(styleTextField);

		fontStylePanel.add(label, BorderLayout.NORTH);
		fontStylePanel.add(innerPanel, BorderLayout.CENTER);
	}
	return fontStylePanel;
}