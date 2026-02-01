    /**
     * Hides the transparency (alpha) slider and related controls in a {@link JColorChooser}.
     * <p>
     * This method uses reflection to access private Swing implementation details in order to
     * disable and hide the transparency controls that are otherwise not configurable via the
     * public API.
     * <p>
     * Adapted from a StackOverflow solution describing how to remove or hide the transparency
     * slider from {@code JColorChooser}.
     *
     * @param chooser the color chooser whose transparency controls should be hidden
     */
    private static void removeTransparencySlider(final JColorChooser chooser) {
        if (chooser == null) {
            return;
        }

        final AbstractColorChooserPanel[] colorPanels = chooser.getChooserPanels();
        if (colorPanels == null || colorPanels.length == 0) {
            return;
        }

        // Index of the transparency spinner in most panels
        final int DEFAULT_TRANSPARENCY_INDEX = 3;
        // Index used by the last panel implementation
        final int LAST_PANEL_TRANSPARENCY_INDEX = 4;

        try {
            for (int i = 1; i < colorPanels.length; i++) {
                final AbstractColorChooserPanel chooserPanel = colorPanels[i];
                if (chooserPanel == null) {
                    continue;
                }

                // Access the private 'panel' field of the chooser panel
                final Field panelField = chooserPanel.getClass().getDeclaredField("panel");
                panelField.setAccessible(true);
                final Object colorPanel = panelField.get(chooserPanel);
                if (colorPanel == null) {
                    continue;
                }

                // Access the private 'spinners' field which holds spinner components
                final Field spinnersField = colorPanel.getClass().getDeclaredField("spinners");
                spinnersField.setAccessible(true);
                final Object spinners = spinnersField.get(colorPanel);
                if (spinners == null || !spinners.getClass().isArray()) {
                    continue;
                }

                final int length = Array.getLength(spinners);
                int transparencyIndex = DEFAULT_TRANSPARENCY_INDEX;
                if (i == colorPanels.length - 1) {
                    transparencyIndex = LAST_PANEL_TRANSPARENCY_INDEX;
                }
                if (transparencyIndex < 0 || transparencyIndex >= length) {
                    continue;
                }

                final Object transparencySpinner = Array.get(spinners, transparencyIndex);
                if (transparencySpinner == null) {
                    continue;
                }

                // Hide the slider component
                final Field sliderField = transparencySpinner.getClass().getDeclaredField("slider");
                sliderField.setAccessible(true);
                final JSlider slider = (JSlider) sliderField.get(transparencySpinner);
                if (slider != null) {
                    slider.setEnabled(false);
                    slider.setVisible(false);
                }

                // Hide the spinner component
                final Field spinnerField = transparencySpinner.getClass().getDeclaredField("spinner");
                spinnerField.setAccessible(true);
                final JSpinner spinner = (JSpinner) spinnerField.get(transparencySpinner);
                if (spinner != null) {
                    spinner.setEnabled(false);
                    spinner.setVisible(false);
                }

                // Hide the label component
                final Field labelField = transparencySpinner.getClass().getDeclaredField("label");
                labelField.setAccessible(true);
                final JLabel label = (JLabel) labelField.get(transparencySpinner);
                if (label != null) {
                    label.setVisible(false);
                }
            }
        } catch (ReflectiveOperationException | SecurityException ex) {
            // Reflection may fail depending on the Swing implementation; ignore to avoid breaking UI
        }
    }