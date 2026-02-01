protected JPanel getFontFamilyPanel() {
	if (fontNamePanel == null) {
		// Lazily initialize and cache the font family panel
		final int PREFERRED_WIDTH = 180;
		final int PREFERRED_HEIGHT = TOP_PANE_PREFERRED_HEIGHT;

		fontNamePanel = new JPanel();
		fontNamePanel.setLayout(new BorderLayout());
		fontNamePanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
		fontNamePanel.setPreferredSize(new java.awt.Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));

		// Accessible label
		javax.swing.JLabel label = new javax.swing.JLabel("Font Name");
		label.setHorizontalAlignment(javax.swing.JLabel.LEFT);
		label.setHorizontalTextPosition(javax.swing.JLabel.LEFT);
		label.setDisplayedMnemonic('F');
		label.setLabelFor(getFontFamilyTextField());

		// Center panel with text field and scrollable list
		javax.swing.JPanel centerPanel = new javax.swing.JPanel();
		centerPanel.setLayout(new java.awt.BorderLayout());
		centerPanel.add(getFontFamilyTextField(), java.awt.BorderLayout.NORTH);

		javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(getFontFamilyList());
		scrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		if (scrollPane.getVerticalScrollBar() != null) {
			scrollPane.getVerticalScrollBar().setFocusable(false);
		}
		centerPanel.add(scrollPane, java.awt.BorderLayout.CENTER);

		fontNamePanel.add(label, java.awt.BorderLayout.NORTH);
		fontNamePanel.add(centerPanel, java.awt.BorderLayout.CENTER);
	}
	return fontNamePanel;
}