@Override public Dimension preferredLayoutSize(Container parent) {
    if (parent == null) {
        return new Dimension();
    }

    Component[] components = parent.getComponents();
    if (components == null) {
        return new Dimension();
    }

    int visibleAmount = Integer.MAX_VALUE;
    final Dimension dimension = new Dimension();

    for (final Component component : components) {
        if (component != null && component.isVisible()) {
            if (component instanceof JScrollBar) {
                final JScrollBar scrollBar = (JScrollBar) component;
                visibleAmount = scrollBar.getVisibleAmount();
            } else {
                final Dimension preferredSize = component.getPreferredSize();
                if (preferredSize != null) {
                    dimension.width = Math.max(dimension.width, preferredSize.width);
                    dimension.height += preferredSize.height;
                }
            }
        }
    }

    final Insets insets = parent.getInsets();
    if (insets != null) {
        dimension.height = Math.min(dimension.height + insets.top + insets.bottom, visibleAmount);
    }

    return dimension;
}