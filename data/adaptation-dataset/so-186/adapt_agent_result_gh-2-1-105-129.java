private void init(final String[] fontSizeStrings) {
	String[] sizes = fontSizeStrings;
	if (sizes == null) {
		sizes = DEFAULT_FONT_SIZE_STRINGS;
	}
	this.fontSizeStrings = sizes;

	JPanel selectPanel = new JPanel();
	selectPanel.setLayout(new BoxLayout(selectPanel, BoxLayout.X_AXIS));
	selectPanel.add(getFontFamilyPanel());
	selectPanel.add(getFontStylePanel());
	selectPanel.add(getFontSizePanel());

	JPanel contentsPanel = new JPanel();
	contentsPanel.setLayout(new BorderLayout());
	contentsPanel.add(selectPanel, BorderLayout.CENTER);
	contentsPanel.add(getSamplePanel(), BorderLayout.SOUTH);

	this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	this.add(contentsPanel);
	this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	this.setSelectedFont(DEFAULT_SELECTED_FONT);
}