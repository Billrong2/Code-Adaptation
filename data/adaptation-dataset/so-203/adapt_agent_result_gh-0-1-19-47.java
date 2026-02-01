public static String renderTextAsAscii(String text) {
    if (text == null || text.trim().isEmpty()) {
        return "";
    }

    final int imageWidth = 144;
    final int imageHeight = 32;
    final int fontSize = 14;
    final String fontName = "Dialog";

    BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
    Graphics2D graphics = null;
    StringBuilder asciiArt = new StringBuilder();

    try {
        graphics = (Graphics2D) image.getGraphics();
        graphics.setFont(new Font(fontName, Font.PLAIN, fontSize));
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.drawString(text, 6, fontSize + 4);

        ImageIO.write(image, "png", new File("text.png"));

        for (int y = 0; y < imageHeight; y++) {
            StringBuilder line = new StringBuilder();
            for (int x = 0; x < imageWidth; x++) {
                int rgb = image.getRGB(x, y);
                line.append(rgb == -16777216 ? ' ' : rgb == -1 ? '#' : '*');
            }
            if (line.toString().trim().isEmpty()) {
                continue;
            }
            if (asciiArt.length() > 0) {
                asciiArt.append('\n');
            }
            asciiArt.append(line);
        }
    } catch (IOException e) {
        throw new RuntimeException("Failed to render text as ASCII art", e);
    } finally {
        if (graphics != null) {
            graphics.dispose();
        }
    }

    return asciiArt.toString();
}