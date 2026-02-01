public AutoSuggestor(final JTextField textField, final Window owner, final java.util.List<String> words, final Color suggestionsTextColor, final Color suggestionFocusedColor, final float popupOpacity) {
        if (textField == null || owner == null || suggestionsTextColor == null || suggestionFocusedColor == null) {
            throw new IllegalArgumentException("AutoSuggestor dependencies must not be null");
        }
        this.textField = textField;
        this.container = owner;
        this.suggestionsTextColor = suggestionsTextColor;
        this.suggestionFocusedColor = suggestionFocusedColor;

        // initialize internal state
        this.typedWord = "";
        this.currentIndexOfSpace = 0;
        this.tW = 0;
        this.tH = 0;

        // initialize dictionary
        this.dictionary.clear();
        if (words != null) {
            for (String w : words) {
                if (w != null && !w.isEmpty()) {
                    this.dictionary.add(w);
                }
            }
        }

        // build suggestions panel
        this.suggestionsPanel = new JPanel(new GridLayout(0, 1));
        this.suggestionsPanel.setBackground(textField.getBackground());

        // create popup window
        try {
            this.autoSuggestionPopUpWindow = new JWindow(owner);
            float safeOpacity = Math.max(0.0f, Math.min(1.0f, popupOpacity));
            try {
                this.autoSuggestionPopUpWindow.setOpacity(safeOpacity);
            } catch (IllegalArgumentException ex) {
                // ignore unsupported opacity values/platforms
            }
        } catch (RuntimeException ex) {
            // owner might not be displayable yet; create a default window
            this.autoSuggestionPopUpWindow = new JWindow();
        }

        this.autoSuggestionPopUpWindow.getContentPane().add(this.suggestionsPanel);
        this.autoSuggestionPopUpWindow.setFocusableWindowState(true);

        // ensure listener is only added once
        boolean alreadyRegistered = false;
        for (DocumentListener dl : this.textField.getDocument().getDocumentListeners()) {
            if (dl == this.documentListener) {
                alreadyRegistered = true;
                break;
            }
        }
        if (!alreadyRegistered) {
            this.textField.getDocument().addDocumentListener(this.documentListener);
        }

        // key bindings for focus navigation inside popup
        addKeyBindingToRequestFocusInPopUpWindow();
    }