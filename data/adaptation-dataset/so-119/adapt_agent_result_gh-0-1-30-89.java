public DraggableTabbedPane() {
    super();

    addMouseMotionListener(new MouseMotionAdapter() {
      @Override
      public void mouseDragged(MouseEvent e) {
        // Begin drag: capture tab image on first drag event
        if (!dragging) {
          if (getUI() == null) {
            return;
          }
          int tabIndex = getUI().tabForCoordinate(DraggableTabbedPane.this, e.getX(), e.getY());
          if (tabIndex >= 0) {
            draggedTabIndex = tabIndex;
            Rectangle tabBounds = getUI().getTabBounds(DraggableTabbedPane.this, tabIndex);
            if (tabBounds == null || tabBounds.width <= 0 || tabBounds.height <= 0) {
              return;
            }

            // Paint the tabbed pane to an offscreen image
            Image fullImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics fullGraphics = fullImage.getGraphics();
            boolean wasDoubleBuffered = isDoubleBuffered();
            try {
              fullGraphics.setClip(tabBounds);
              // Disable double buffering when painting to a static image
              setDoubleBuffered(false);
              paintComponent(fullGraphics);
            } finally {
              // Restore double buffering and dispose graphics
              setDoubleBuffered(wasDoubleBuffered);
              fullGraphics.dispose();
            }

            // Extract just the dragged tab image
            BufferedImage draggedImage = new BufferedImage(tabBounds.width, tabBounds.height, BufferedImage.TYPE_INT_ARGB);
            Graphics tabGraphics = draggedImage.getGraphics();
            try {
              tabGraphics.drawImage(
                  fullImage,
                  0,
                  0,
                  tabBounds.width,
                  tabBounds.height,
                  tabBounds.x,
                  tabBounds.y,
                  tabBounds.x + tabBounds.width,
                  tabBounds.y + tabBounds.height,
                  DraggableTabbedPane.this);
            } finally {
              tabGraphics.dispose();
            }

            tabImage = draggedImage;
            currentMouseLocation = e.getPoint();
            dragging = true;
            repaint();
          }
        } else {
          // Continue drag: update mouse location
          currentMouseLocation = e.getPoint();
          repaint();
        }
        super.mouseDragged(e);
      }
    });

    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseReleased(MouseEvent e) {
        // Finish drag: move tab if applicable
        if (dragging && getUI() != null) {
          int releaseY = e.getY(); // computed value instead of magic constant
          int targetIndex = getUI().tabForCoordinate(DraggableTabbedPane.this, e.getX(), releaseY);
          if (targetIndex >= 0 && draggedTabIndex >= 0 && draggedTabIndex < getTabCount()) {
            Component component = getComponentAt(draggedTabIndex);
            String title = getTitleAt(draggedTabIndex);
            removeTabAt(draggedTabIndex);
            insertTab(title, null, component, null, targetIndex);
          }
        }

        // Reset drag state
        dragging = false;
        tabImage = null;
        currentMouseLocation = null;
      }
    });
  }