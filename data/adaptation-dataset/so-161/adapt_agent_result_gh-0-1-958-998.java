public void interactiveTextAreaRendererList() {
    final javax.swing.DefaultListModel model = new javax.swing.DefaultListModel();
    model.addElement("Item: short text");
    model.addElement("Item: long text. This is a long text. This is a long text. This is a long text. This is a long text. This is a long text. This is a long text.");
    model.addElement("Item: even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text.");

    final org.jdesktop.swingx.JXList list = new org.jdesktop.swingx.JXList(model);
    list.setScrollableTracksViewportWidth(true);

    final org.jdesktop.swingx.renderer.TextAreaProvider provider = new org.jdesktop.swingx.renderer.TextAreaProvider();
    final org.jdesktop.swingx.renderer.DefaultListRenderer renderer = new org.jdesktop.swingx.renderer.DefaultListRenderer(provider);
    list.setCellRenderer(renderer);

    list.addComponentListener(new java.awt.event.ComponentAdapter() {
        @Override
        public void componentResized(java.awt.event.ComponentEvent e) {
            list.invalidateCellSizeCache();
        }
    });

    org.jdesktop.swingx.JXFrame frame = showWithScrollingInFrame(list, "JXList with wrapped text");
    frame.pack();
    frame.setVisible(true);
}