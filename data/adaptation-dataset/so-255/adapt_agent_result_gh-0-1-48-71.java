private void initComponent() {
        // basic hardening
        if (textField == null) {
            return;
        }
        // make focusable and style
        setFocusable(true);
        if (suggestionsTextColor != null) {
            setForeground(suggestionsTextColor);
        }

        // central apply-and-hide behavior
        final Runnable applyAndHide = new Runnable() {
            @Override
            public void run() {
                if (autoSuggestionsPopUpWindow == null) {
                    return;
                }
                String value = getText();
                if (value == null || value.trim().isEmpty()) {
                    return;
                }
                replaceWithSuggestedText();
                autoSuggestionsPopUpWindow.setVisible(false);
            }
        };

        // mouse interaction
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                applyAndHide.run();
            }
        });

        // Enter key binding on key release
        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), "acceptSuggestion");
        getActionMap().put("acceptSuggestion", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyAndHide.run();
            }
        });
    }