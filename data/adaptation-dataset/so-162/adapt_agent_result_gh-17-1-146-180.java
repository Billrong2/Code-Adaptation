private static BufferedImage toBufferedImage(final Image image) {
	if (image == null) {
		return null;
	}
	if (image instanceof BufferedImage) {
		return (BufferedImage) image;
	}
	final Image loadedImage = new ImageIcon(image).getImage();
	final int width = loadedImage.getWidth(null);
	final int height = loadedImage.getHeight(null);
	if (width <= 0 || height <= 0) {
		return null;
	}
	final boolean hasAlpha = hasAlpha(loadedImage);
	BufferedImage bufferedImage = null;
	try {
		final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		final GraphicsDevice gs = ge.getDefaultScreenDevice();
		final GraphicsConfiguration gc = gs.getDefaultConfiguration();
		final int transparency = hasAlpha ? Transparency.BITMASK : Transparency.OPAQUE;
		bufferedImage = gc.createCompatibleImage(width, height, transparency);
	} catch (HeadlessException e) {
	}
	if (bufferedImage == null) {
		final int type = hasAlpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
		bufferedImage = new BufferedImage(width, height, type);
	}
	final Graphics graphics = bufferedImage.createGraphics();
	try {
		graphics.drawImage(loadedImage, 0, 0, null);
	} finally {
		graphics.dispose();
	}
	return bufferedImage;
}