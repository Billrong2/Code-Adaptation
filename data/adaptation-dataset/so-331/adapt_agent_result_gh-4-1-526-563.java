protected JDialog createDialog(Component parent) {
        // derive parent Frame safely
        Frame owner = null;
        if (parent != null) {
            if (parent instanceof Frame) {
                owner = (Frame) parent;
            } else {
                Component c = parent;
                while (c != null) {
                    if (c instanceof Frame) {
                        owner = (Frame) c;
                        break;
                    }
                    c = c.getParent();
                }
            }
        }

        final JDialog dialog = new JDialog(owner, "Select Font", true);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        // actions
        final Action okAction = new DialogOKAction(dialog);
        final Action cancelAction = new DialogCancelAction(dialog);

        // buttons
        final JButton okButton = new JButton(okAction);
        final JButton cancelButton = new JButton(cancelAction);
        okButton.setFocusable(false);
        cancelButton.setFocusable(false);
        okButton.setFont(DEFAULT_FONT);
        cancelButton.setFont(DEFAULT_FONT);

        // button panel (east)
        final JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.add(okButton);
        buttonPanel.add(Box.createVerticalStrut(5));
        buttonPanel.add(cancelButton);

        // content layout
        final Container content = dialog.getContentPane();
        content.setLayout(new BorderLayout());
        content.add(this, BorderLayout.CENTER);
        content.add(buttonPanel, BorderLayout.EAST);
        ((JComponent) content).setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // key bindings
        final JRootPane rootPane = dialog.getRootPane();
        final InputMap im = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        final ActionMap am = rootPane.getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), DialogCancelAction.ACTION_NAME);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), DialogOKAction.ACTION_NAME);
        am.put(DialogCancelAction.ACTION_NAME, cancelAction);
        am.put(DialogOKAction.ACTION_NAME, okAction);

        dialog.pack();
        dialog.setLocationRelativeTo(owner);
        return dialog;
    }