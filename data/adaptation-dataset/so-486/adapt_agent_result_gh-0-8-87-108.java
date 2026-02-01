private Dimension computeMinSize(Container target) {
    if (target == null) {
        return new Dimension(0, 0);
    }
    synchronized (target.getTreeLock()) {
        int minWidth = Integer.MAX_VALUE;
        int minHeight = Integer.MAX_VALUE;
        boolean foundVisible = false;

        int n = target.getComponentCount();
        for (int i = 0; i < n; i++) {
            Component c = target.getComponent(i);
            if (c != null && c.isVisible()) {
                Dimension d = c.getPreferredSize();
                if (d != null) {
                    minWidth = Math.min(minWidth, d.width);
                    minHeight = Math.min(minHeight, d.height);
                    foundVisible = true;
                }
            }
        }

        if (!foundVisible) {
            return new Dimension(0, 0);
        }
        return new Dimension(minWidth, minHeight);
    }
}