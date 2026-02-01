@Override
public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {

  final int INSET = 2;
  final int POINTER_PAD = 4;

  if (width <= 0 || height <= 0) {
    return;
  }

  Graphics2D g2 = (Graphics2D) g;

  // Adjusted bottom line to avoid overlap with stroke
  int bottomLineY = height - thickness - pointerSize - 1;

  // Apply inset to bubble geometry
  int bubbleX = INSET + strokePad;
  int bubbleY = INSET + strokePad;
  int bubbleW = width - (INSET * 2) - thickness;
  int bubbleH = bottomLineY - INSET;

  // Guard against invalid geometry
  if (bubbleW <= 0 || bubbleH <= 0) {
    return;
  }

  RoundRectangle2D.Double bubble = new RoundRectangle2D.Double(
    bubbleX,
    bubbleY,
    bubbleW,
    bubbleH,
    radii,
    radii
  );

  // Build area starting with the rounded rectangle
  Area area = new Area(bubble);

  // Conditionally add the speech pointer
  if (pointerSize > 0) {
    java.awt.Polygon pointer = new java.awt.Polygon();

    if (pointerLeft) {
      // left point
      pointer.addPoint(
        bubbleX + radii + POINTER_PAD,
        bottomLineY
      );
      // right point
      pointer.addPoint(
        bubbleX + radii + POINTER_PAD + pointerSize,
        bottomLineY
      );
      // bottom point
      pointer.addPoint(
        bubbleX + radii + POINTER_PAD + (pointerSize / 2),
        height - strokePad
      );
    } else {
      // left point
      pointer.addPoint(
        width - (bubbleX + radii + POINTER_PAD),
        bottomLineY
      );
      // right point
      pointer.addPoint(
        width - (bubbleX + radii + POINTER_PAD + pointerSize),
        bottomLineY
      );
      // bottom point
      pointer.addPoint(
        width - (bubbleX + radii + POINTER_PAD + (pointerSize / 2)),
        height - strokePad
      );
    }

    area.add(new Area(pointer));
  }

  g2.setRenderingHints(hints);

  // Paint the BG color of the parent everywhere outside the bubble clip
  Component parent = c.getParent();
  if (parent != null) {
    Color bg = parent.getBackground();
    Rectangle rect = new Rectangle(0, 0, width, height);
    Area borderRegion = new Area(rect);
    borderRegion.subtract(area);
    g2.setClip(borderRegion);
    g2.setColor(bg);
    g2.fillRect(0, 0, width, height);
    g2.setClip(null);
  }

  g2.setColor(color);
  g2.setStroke(stroke);
  g2.draw(area);
}