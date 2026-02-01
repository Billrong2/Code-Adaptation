@Override
public void paintBorder(
        final Component c,
        final Graphics g,
        final int x,
        final int y,
        final int width,
        final int height)
{
    if (!(g instanceof Graphics2D))
    {
        return;
    }

    if (width <= 0 || height <= 0 || thickness < 0 || pointerSize < 0)
    {
        return;
    }

    final Graphics2D g2 = (Graphics2D) g;

    final int bottomLineY = height - thickness - pointerSize;

    final RoundRectangle2D.Double bubble = new RoundRectangle2D.Double(
            strokePad,
            strokePad,
            width - thickness,
            bottomLineY,
            radii,
            radii);

    final Polygon pointer = new Polygon();

    if (left)
    {
        // left point
        pointer.addPoint(
                strokePad + radii + pointerPad,
                bottomLineY);
        // right point
        pointer.addPoint(
                strokePad + radii + pointerPad + pointerSize,
                bottomLineY);
        // bottom point
        pointer.addPoint(
                strokePad + radii + pointerPad + (pointerSize / 2),
                height - strokePad);
    }
    else
    {
        // left point
        pointer.addPoint(
                width - (strokePad + radii + pointerPad),
                bottomLineY);
        // right point
        pointer.addPoint(
                width - (strokePad + radii + pointerPad + pointerSize),
                bottomLineY);
        // bottom point
        pointer.addPoint(
                width - (strokePad + radii + pointerPad + (pointerSize / 2)),
                height - strokePad);
    }

    final Area area = new Area(bubble);
    area.add(new Area(pointer));

    g2.setRenderingHints(hints);

    // Paint the BG color of the parent everywhere outside the clip of the text bubble
    final Component parent = c != null ? c.getParent() : null;
    if (parent != null)
    {
        final Color bg = parent.getBackground();
        final Rectangle rect = new Rectangle(0, 0, width, height);
        final Area borderRegion = new Area(rect);
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