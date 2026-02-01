protected JPanel getFontStylePanel() {
		if (fontStylePanel == null) {
			// Lazy initialization of the font style panel
			fontStylePanel = new JPanel();
			fontStylePanel.setLayout(new BorderLayout());
			fontStylePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

			// Scrollable list of font styles
			JScrollPane scrollPane = new JScrollPane(getFontStyleList());
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			scrollPane.getVerticalScrollBar().setFocusable(false);

			// Nested panel containing text field and list
			JPanel contentPanel = new JPanel();
			contentPanel.setLayout(new BorderLayout());
			contentPanel.add(getFontStyleTextField(), BorderLayout.NORTH);
			contentPanel.add(scrollPane, BorderLayout.CENTER);

			// Label with mnemonic and accessibility binding
			JLabel label = new JLabel(("Font Style"));
			label.setHorizontalAlignment(JLabel.LEFT);
			label.setHorizontalTextPosition(JLabel.LEFT);
			label.setDisplayedMnemonic('Y');
			label.setLabelFor(getFontStyleTextField());

			// Preferred size with fixed height
			fontStylePanel.setPreferredSize(new Dimension(
					label.getPreferredSize().width + 10,
					TOP_PANE_PREFERRED_HEIGHT));

			fontStylePanel.add(label, BorderLayout.NORTH);
			fontStylePanel.add(contentPanel, BorderLayout.CENTER);
		}
		return fontStylePanel;
	}