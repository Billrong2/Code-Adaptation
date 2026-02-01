protected void paintThumb(java.awt.Graphics g) {
	if (g == null || thumbRect == null) {
		return;
	}
	final java.awt.Graphics2D g2d = (java.awt.Graphics2D) g.create();
	try {
		final Object oldAntialias = g2d.getRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING);
		final java.awt.Color oldColor = g2d.getColor();
		g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
		final java.awt.Rectangle t = thumbRect;
		g2d.setColor(java.awt.Color.black);
		final int halfThumbWidth = t.width / 2;
		final int edgeOffset = 1;
		final int leftX = t.x;
		final int rightX = t.x + t.width - edgeOffset;
		final int topY = t.y;
		final int bottomY = t.y + t.height;
		final int centerX = t.x + halfThumbWidth;
		g2d.drawLine(leftX, topY, rightX, topY);
		g2d.drawLine(leftX, topY, centerX, bottomY);
		g2d.drawLine(rightX, topY, centerX, bottomY);
		g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, oldAntialias);
		g2d.setColor(oldColor);
	} finally {
		g2d.dispose();
	}
}