private void addKeyBindingToRequestFocusInPopUpWindow() {
        // Bind DOWN key on text field to move focus into suggestions popup
        if (textField == null) {
            return;
        }

        javax.swing.InputMap tfInputMap = textField.getInputMap(JComponent.WHEN_FOCUSED);
        javax.swing.ActionMap tfActionMap = textField.getActionMap();

        tfInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "as_moveFocusToPopup");
        tfActionMap.put("as_moveFocusToPopup", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (autoSuggestionPopUpWindow == null || suggestionsPanel == null) {
                    return;
                }
                ArrayList<SuggestionLabel> labels = getAddedSuggestionLabels();
                if (labels == null || labels.isEmpty()) {
                    return;
                }

                // ensure popup is visible and on top
                autoSuggestionPopUpWindow.setVisible(true);
                autoSuggestionPopUpWindow.toFront();
                autoSuggestionPopUpWindow.requestFocusInWindow();
                suggestionsPanel.requestFocusInWindow();

                // focus first suggestion
                for (SuggestionLabel sl : labels) {
                    sl.setFocused(false);
                }
                SuggestionLabel first = labels.get(0);
                first.setFocused(true);
                first.requestFocusInWindow();

                // track focused index via client property
                suggestionsPanel.putClientProperty("focusedIndex", 0);
            }
        });

        // Bind DOWN key inside suggestions panel to iterate suggestions
        suggestionsPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "as_advanceFocus");
        suggestionsPanel.getActionMap().put("as_advanceFocus", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (autoSuggestionPopUpWindow == null || suggestionsPanel == null) {
                    return;
                }
                ArrayList<SuggestionLabel> labels = getAddedSuggestionLabels();
                if (labels == null || labels.isEmpty()) {
                    return;
                }

                Object idxObj = suggestionsPanel.getClientProperty("focusedIndex");
                int focusedIndex = (idxObj instanceof Integer) ? (Integer) idxObj : -1;

                // if only one suggestion or we passed the last one, reset
                if (labels.size() == 1 || focusedIndex >= labels.size() - 1) {
                    autoSuggestionPopUpWindow.setVisible(false);
                    suggestionsPanel.putClientProperty("focusedIndex", -1);
                    setFocusToTextField();
                    checkForAndShowSuggestions();
                    return;
                }

                // advance focus
                if (focusedIndex >= 0) {
                    labels.get(focusedIndex).setFocused(false);
                }
                int nextIndex = focusedIndex + 1;
                SuggestionLabel next = labels.get(nextIndex);
                next.setFocused(true);
                next.requestFocusInWindow();
                suggestionsPanel.putClientProperty("focusedIndex", nextIndex);
            }
        });
    }