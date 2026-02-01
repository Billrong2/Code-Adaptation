protected JPanel getFontFamilyPanel() {
	if (fontNamePanel == null) {
		// Lazily create and cache the font family panel
		fontNamePanel = new JPanel();
		fontNamePanel.setLayout(new BorderLayout());
		fontNamePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		fontNamePanel.setPreferredSize(new Dimension(200, TOP_PANE_PREFERRED_HEIGHT));

		// Ensure dependent components are initialized
		JTextField familyTextField = getFontFamilyTextField();
		JList familyList = getFontFamilyList();

		// Scrollable list configuration
		JScrollPane scrollPane = new JScrollPane(familyList);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		if (scrollPane.getVerticalScrollBar() != null) {
			scrollPane.getVerticalScrollBar().setFocusable(false);
		}

		// Subpanel holding text field (NORTH) and list (CENTER)
		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.add(familyTextField, BorderLayout.NORTH);
		contentPanel.add(scrollPane, BorderLayout.CENTER);

		// Accessible label
		JLabel label = new JLabel("Font Name");
		label.setHorizontalAlignment(JLabel.LEFT);
		label.setHorizontalTextPosition(JLabel.LEFT);
		label.setDisplayedMnemonic('F');
		label.setLabelFor(familyTextField);

		fontNamePanel.add(label, BorderLayout.NORTH);
		fontNamePanel.add(contentPanel, BorderLayout.CENTER);
	}
	return fontNamePanel;
}