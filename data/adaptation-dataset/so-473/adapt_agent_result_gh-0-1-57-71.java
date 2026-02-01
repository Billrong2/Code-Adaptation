/**
 * Applies a color dye over the given image using alpha compositing.
 * <p>
 * Source: Stack Overflow (link to be filled later)
 * </p>
 *
 * @param image the source image to dye
 * @param color the color to apply
 * @return a new {@link java.awt.image.BufferedImage} containing the dyed image
 */
public static java.awt.image.BufferedImage dye(java.awt.image.BufferedImage image, java.awt.Color color) {
	int w = image.getWidth();
	int h = image.getHeight();
	java.awt.image.BufferedImage dyed = new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB);
	java.awt.Graphics2D g = dyed.createGraphics();
	g.drawImage(image, 0, 0, null);
	g.setComposite(java.awt.AlphaComposite.SrcAtop);
	g.setColor(color);
	g.fillRect(0, 0, w, h);
	g.dispose();
	return dyed;
}