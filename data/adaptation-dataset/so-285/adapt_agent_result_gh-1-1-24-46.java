public static BufferedImage stringToBufferedImage(String s, Font font) {
    if (s == null) {
        throw new IllegalArgumentException("String must not be null");
    }
    if (font == null) {
        throw new IllegalArgumentException("Font must not be null");
    }

    // First, calculate the string's width and height
    BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
    Graphics g = img.getGraphics();
    Rectangle2D rect;
    try {
        // Use the provided font for measuring
        g.setFont(font);
        FontRenderContext frc = g.getFontMetrics().getFontRenderContext();
        rect = font.getStringBounds(s, frc);
    } finally {
        // Release resources
        g.dispose();
    }

    // Then, draw the string on the final image
    img = new BufferedImage((int) Math.ceil(rect.getWidth()), (int) Math.ceil(rect.getHeight()), BufferedImage.TYPE_4BYTE_ABGR);
    g = img.getGraphics();
    try {
        g.setColor(Color.black); // Otherwise the text would be white
        g.setFont(font);

        // Calculate x and y for that string
        FontMetrics fm = g.getFontMetrics();
        int x = 0;
        int y = fm.getAscent(); // getAscent() = baseline
        g.drawString(s, x, y);
    } finally {
        // Release resources
        g.dispose();
    }

    // Return the image
    return img;
}