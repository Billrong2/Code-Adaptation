@Override public Dimension minimumLayoutSize(Container parent) {
    if (parent == null) {
        return new Dimension();
    }

    Component[] components = parent.getComponents();
    if (components == null) {
        return new Dimension();
    }

    int visibleAmount = Integer.MAX_VALUE;
    Dimension resultDim = new Dimension();

    for (Component comp : components) {
        if (comp != null && comp.isVisible()) {
            if (comp instanceof JScrollBar) {
                // Use the visible amount of the (vertical) scrollbar to cap height
                JScrollBar scrollBar = (JScrollBar) comp;
                visibleAmount = scrollBar.getVisibleAmount();
            } else {
                Dimension min = comp.getMinimumSize();
                if (min != null) {
                    resultDim.width = Math.max(resultDim.width, min.width);
                    resultDim.height += min.height;
                }
            }
        }
    }

    Insets insets = parent.getInsets();
    if (insets != null) {
        resultDim.height = Math.min(resultDim.height + insets.top + insets.bottom, visibleAmount);
    } else {
        resultDim.height = Math.min(resultDim.height, visibleAmount);
    }

    return resultDim;
}