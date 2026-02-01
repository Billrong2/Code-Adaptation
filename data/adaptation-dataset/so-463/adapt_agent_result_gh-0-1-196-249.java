private void processClick(java.awt.event.MouseEvent event)
{
    if (event == null || !(event.getSource() instanceof javax.swing.text.JTextComponent))
    {
        return;
    }

    jTextComponent = (javax.swing.text.JTextComponent) event.getSource();

    boolean enableUndo = undoManager.canUndo();
    boolean enableRedo = undoManager.canRedo();
    boolean enableCut = false;
    boolean enableCopy = false;
    boolean enablePaste = false;
    boolean enableDelete = false;
    boolean enableSelectAll = false;

    String selectedText = jTextComponent.getSelectedText();
    String text = jTextComponent.getText();

    if (text != null && text.length() > 0)
    {
        enableSelectAll = true;
    }

    if (selectedText != null && selectedText.length() > 0)
    {
        enableCut = true;
        enableCopy = true;
        enableDelete = true;
    }

    // Determine paste availability by attempting to retrieve string data from the clipboard
    if (clipboard != null)
    {
        try
        {
            java.awt.datatransfer.Transferable transferable = clipboard.getContents(null);
            if (transferable != null)
            {
                Object data = transferable.getTransferData(java.awt.datatransfer.DataFlavor.stringFlavor);
                if (data != null)
                {
                    enablePaste = true;
                }
            }
        }
        catch (java.awt.datatransfer.UnsupportedFlavorException | java.io.IOException ex)
        {
            ex.printStackTrace();
            enablePaste = false;
        }
    }

    undo.setEnabled(enableUndo);
    redo.setEnabled(enableRedo);
    cut.setEnabled(enableCut);
    copy.setEnabled(enableCopy);
    paste.setEnabled(enablePaste);
    delete.setEnabled(enableDelete);
    selectAll.setEnabled(enableSelectAll);

    show(jTextComponent, event.getX(), event.getY());
}