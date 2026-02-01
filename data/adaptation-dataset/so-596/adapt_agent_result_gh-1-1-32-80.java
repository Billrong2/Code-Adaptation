private Dimension computeSize(Container target) {
  synchronized (target.getTreeLock()) {
    int hgap = getHgap();
    int vgap = getVgap();
    int containerWidth = target.getWidth();

    // Let this behave like a regular FlowLayout (single row) if the container
    // hasn't been assigned any size yet.
    if (containerWidth == 0) {
      containerWidth = Integer.MAX_VALUE;
    }

    Insets insets = target.getInsets();
    if (insets == null) {
      insets = new Insets(0, 0, 0, 0);
    }

    int requiredWidth = 0;
    int maxWidth = containerWidth - (insets.left + insets.right + hgap * 2);
    int componentCount = target.getComponentCount();

    int x = 0;
    // FlowLayout starts by adding vgap, so do that here too.
    int y = insets.top + vgap;
    int rowHeight = 0;

    for (int i = 0; i < componentCount; i++) {
      Component component = target.getComponent(i);
      if (component.isVisible()) {
        Dimension preferredSize = component.getPreferredSize();
        if ((x == 0) || ((x + preferredSize.width) <= maxWidth)) {
          // Fits in the current row.
          if (x > 0) {
            x += hgap;
          }
          x += preferredSize.width;
          rowHeight = Math.max(rowHeight, preferredSize.height);
        } else {
          // Start a new row.
          x = preferredSize.width;
          y += vgap + rowHeight;
          rowHeight = preferredSize.height;
        }
        requiredWidth = Math.max(requiredWidth, x);
      }
    }

    y += rowHeight;
    y += insets.bottom;

    return new Dimension(requiredWidth + insets.left + insets.right, y);
  }
}