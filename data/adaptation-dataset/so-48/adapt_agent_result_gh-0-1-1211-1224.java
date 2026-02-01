  /**
   * Returns the maximum usable window bounds for the default screen.
   * <p>
   * This method is functionally equivalent to
   * {@link java.awt.GraphicsEnvironment#getMaximumWindowBounds()}, but has been
   * found to be more reliable after display or screen mode changes (such as
   * resolution switches or full-screen transitions).
   * </p>
   * <p>
   * The calculation explicitly accounts for screen insets (taskbars, docks,
   * system UI) using the current {@link java.awt.DisplayMode}.
   * </p>
   * <p>
   * Reference:
   * https://stackoverflow.com/questions/1932497/java-getmaximumwindowbounds-after-displaymode-change
   * </p>
   *
   * @return a {@link java.awt.Rectangle} representing the maximum usable screen area
   */
  public static Rectangle getMaximumBounds() {
    Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(
      GraphicsEnvironment.getLocalGraphicsEnvironment()
        .getDefaultScreenDevice()
        .getDefaultConfiguration());

    DisplayMode mode = GraphicsEnvironment.getLocalGraphicsEnvironment()
      .getDefaultScreenDevice()
      .getDisplayMode();

    Rectangle bounds = new Rectangle();
    bounds.x = insets.left;
    bounds.y = insets.top;
    bounds.width = mode.getWidth() - (insets.left + insets.right);
    bounds.height = mode.getHeight() - (insets.top + insets.bottom);
    return bounds;
  }