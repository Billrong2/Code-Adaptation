protected JPanel getFontFamilyPanel() {
	if (fontNamePanel != null) {
		return fontNamePanel;
	}
	// Lazy initialization of font family panel
	fontNamePanel = new JPanel();
	fontNamePanel.setLayout(new BorderLayout());
	fontNamePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	fontNamePanel.setPreferredSize(new Dimension(140, TOP_PANE_PREFERRED_HEIGHT));

	// Ensure dependent components are initialized
	JTextField textField = getFontFamilyTextField();	// initializes if null
	JList list = getFontFamilyList();			// initializes if null

	// Scroll pane for font family list
	JScrollPane scrollPane = new JScrollPane(list);
	scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	scrollPane.getVerticalScrollBar().setFocusable(false);

	// Inner panel holding text field and list
	JPanel innerPanel = new JPanel(new BorderLayout());
	innerPanel.add(textField, BorderLayout.NORTH);
	innerPanel.add(scrollPane, BorderLayout.CENTER);

	// Accessible label
	JLabel label = new JLabel("Font Name");
	label.setHorizontalAlignment(SwingConstants.LEFT);
	label.setHorizontalTextPosition(SwingConstants.LEFT);
	label.setDisplayedMnemonic('F');
	label.setLabelFor(textField);

	fontNamePanel.add(label, BorderLayout.NORTH);
	fontNamePanel.add(innerPanel, BorderLayout.CENTER);

	return fontNamePanel;
}