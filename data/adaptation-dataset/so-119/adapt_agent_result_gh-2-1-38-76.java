public void mouseDragged(MouseEvent e) {
    // Always track current mouse location at the start
    currentMouseLocation = e.getPoint();

    // Guard against invalid UI state
    if (getUI() == null || getTabCount() <= 0) {
        super.mouseDragged(e);
        return;
    }

    // Constant for Y-offset when querying tab bounds (avoid magic numbers)
    final int TAB_QUERY_Y_OFFSET = e.getY();

    if (!dragging) {
        // Determine hovered tab index
        int tabNumber = getUI().tabForCoordinate(DraggableTabbedPane.this, e.getX(), TAB_QUERY_Y_OFFSET);

        // Prevent initiating drag on first or last tab
        if (tabNumber > 0 && tabNumber < getTabCount() - 1) {
            draggedTabIndex = tabNumber;
            Rectangle bounds = getUI().getTabBounds(DraggableTabbedPane.this, tabNumber);
            if (bounds != null) {
                // Paint the tabbed pane to a buffer
                Image totalImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics totalGraphics = totalImage.getGraphics();
                boolean wasDoubleBuffered = isDoubleBuffered();
                try {
                    totalGraphics.setClip(bounds);
                    // Don't be double buffered when painting to a static image
                    setDoubleBuffered(false);
                    paintComponent(totalGraphics);
                } finally {
                    // Restore double buffering and dispose graphics
                    setDoubleBuffered(wasDoubleBuffered);
                    totalGraphics.dispose();
                }

                // Paint just the dragged tab to the buffer
                tabImage = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
                Graphics graphics = tabImage.getGraphics();
                try {
                    graphics.drawImage(
                            totalImage,
                            0,
                            0,
                            bounds.width,
                            bounds.height,
                            bounds.x,
                            bounds.y,
                            bounds.x + bounds.width,
                            bounds.y + bounds.height,
                            DraggableTabbedPane.this
                    );
                } finally {
                    graphics.dispose();
                }

                dragging = true;
                repaint();
            }
        }
    } else {
        // While dragging, determine potential placement
        TabType potential = getTabType(e);
        if (potential != null && potential.getType() != TabTypeEnum.TAB_NEITHER) {
            toPlace = potential;
        } else {
            toPlace = null;
        }
        repaint();
    }

    super.mouseDragged(e);
}