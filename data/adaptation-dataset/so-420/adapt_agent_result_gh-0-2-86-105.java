/**
 * Scales a {@link java.awt.image.BufferedImage} by the given ratio using an affine transform.
 * <p>
 * Source: Stack Overflow (image scaling using {@link java.awt.geom.AffineTransform}).
 * </p>
 *
 * @param source the source image to scale; must not be {@code null}
 * @param ratio  scale ratio (e.g. {@code 0.5} for 50%); must be {@code > 0}
 * @return a new {@link java.awt.image.BufferedImage} containing the scaled image
 * @throws IllegalArgumentException if {@code source} is {@code null} or {@code ratio <= 0}
 */
private static BufferedImage scale(final BufferedImage source, final double ratio) {
	if (source == null) {
		throw new IllegalArgumentException("source image must not be null");
	}
	if (ratio <= 0) {
		throw new IllegalArgumentException("ratio must be > 0");
	}

	final int w = (int) (source.getWidth() * ratio);
	final int h = (int) (source.getHeight() * ratio);

	final BufferedImage bi = getCompatibleImage(w, h);
	final Graphics2D g2d = bi.createGraphics();
	try {
		final double xScale = (double) w / source.getWidth();
		final double yScale = (double) h / source.getHeight();
		final AffineTransform at = AffineTransform.getScaleInstance(xScale, yScale);
		g2d.drawRenderedImage(source, at);
	}
	finally {
		g2d.dispose();
	}
	return bi;
}