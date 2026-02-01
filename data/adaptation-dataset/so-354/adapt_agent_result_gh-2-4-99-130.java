@Override
public void mousePressed(MouseEvent e) {
    if (e == null || popup == null) {
        return;
    }

    // Only react to right-click / popup trigger
    if (!e.isPopupTrigger() && (e.getModifiersEx() & InputEvent.BUTTON3_DOWN_MASK) == 0) {
        return;
    }

    Object source = e.getSource();
    if (!(source instanceof JTextComponent)) {
        return;
    }

    textComponent = (JTextComponent) source;

    // Request focus safely
    if (textComponent.isEnabled()) {
        textComponent.requestFocusInWindow();
    }

    boolean isEnabled = textComponent.isEnabled();
    boolean isEditable = textComponent.isEditable();
    boolean hasSelection = textComponent.getSelectionStart() != textComponent.getSelectionEnd();
    boolean hasText = textComponent.getDocument() != null && textComponent.getDocument().getLength() > 0;

    // Determine clipboard string availability
    boolean hasClipboardString = false;
    try {
        java.awt.datatransfer.Transferable t = Toolkit.getDefaultToolkit()
                .getSystemClipboard().getContents(null);
        if (t != null && t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            Object data = t.getTransferData(DataFlavor.stringFlavor);
            hasClipboardString = data instanceof String && !((String) data).isEmpty();
        }
    } catch (IllegalStateException | java.awt.datatransfer.UnsupportedFlavorException | java.io.IOException ex) {
        hasClipboardString = false;
    }

    // Enable/disable actions dynamically
    if (undoAction != null) {
        boolean undoAllowed = (lastActionSelected == Actions.CUT || lastActionSelected == Actions.PASTE)
                && isEnabled && isEditable;
        undoAction.setEnabled(undoAllowed);
    }

    if (cutAction != null) {
        cutAction.setEnabled(isEnabled && isEditable && hasSelection);
    }

    if (copyAction != null) {
        copyAction.setEnabled(hasSelection);
    }

    if (pasteAction != null) {
        pasteAction.setEnabled(isEnabled && isEditable && hasClipboardString);
    }

    if (selectAllAction != null) {
        selectAllAction.setEnabled(isEnabled && hasText);
    }

    // Compute popup position relative to component and keep within bounds
    int x = e.getX();
    int y = e.getY();

    java.awt.Dimension popupSize = popup.getPreferredSize();
    if (popupSize != null) {
        int compWidth = textComponent.getWidth();
        int compHeight = textComponent.getHeight();

        if (x + popupSize.width > compWidth) {
            x = Math.max(0, compWidth - popupSize.width);
        }

        // Place above cursor if possible
        if (y + popupSize.height > compHeight) {
            y = Math.max(0, y - popupSize.height);
        }
    }

    popup.show(textComponent, x, y);
}