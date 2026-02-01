@Override
public void paint(Graphics g, JComponent c) {
	super.paint(g, c);
	if (g == null || c == null) {
		return;
	}
	if (!(c instanceof JLayer<?>)) {
		return;
	}
	final JLayer<?> layer = (JLayer<?>) c;
	final Object view = layer.getView();
	if (!(view instanceof JTabbedPane)) {
		return;
	}
	final JTabbedPane tabPane = (JTabbedPane) view;
	if (button == null || pt == null || p0 == null) {
		return;
	}
	final int inset = 2;
	for (int i = 0; i < tabPane.getTabCount(); i++) {
		final Rectangle rect = tabPane.getBoundsAt(i);
		if (rect == null) {
			continue;
		}
		final Dimension d = button.getPreferredSize();
		if (d == null) {
			continue;
		}
		final int x = rect.x + rect.width - d.width - inset;
		final int y = rect.y + (rect.height - d.height) / 2;
		final Rectangle r = new Rectangle(x, y, d.width, d.height);
		button.setForeground(r.contains(pt) ? Color.RED : Color.BLACK);
		SwingUtilities.paintComponent(g, button, p0, r);
	}
}