@Override
public void setBoundsForFrame(JComponent f, int newX, int newY, int newWidth, int newHeight) {
    boolean didResize = (f.getWidth() != newWidth || f.getHeight() != newHeight);

    // Safely determine parent dimensions
    java.awt.Container parent = f.getParent();
    if (!(f instanceof JInternalFrame) || parent == null) {
        f.setBounds(newX, newY, newWidth, newHeight);
        if (didResize) {
            f.validate();
        }
        return;
    }

    JInternalFrame frame = (JInternalFrame) f;

    if (!inBounds(frame, newX, newY, newWidth, newHeight)) {
        java.awt.Dimension parentSize = parent.getSize();

        int parentWidth = Math.max(0, parentSize.width);
        int parentHeight = Math.max(0, parentSize.height);

        // Clamp width/height so they never exceed the parent
        int adjustedWidth = Math.min(newWidth, parentWidth);
        int adjustedHeight = Math.min(newHeight, parentHeight);

        // Recalculate and clamp X/Y using adjusted size
        int maxX = Math.max(0, parentWidth - adjustedWidth);
        int maxY = Math.max(0, parentHeight - adjustedHeight);

        int boundedX = Math.min(Math.max(0, newX), maxX);
        int boundedY = Math.min(Math.max(0, newY), maxY);

        f.setBounds(boundedX, boundedY, adjustedWidth, adjustedHeight);
    } else {
        // Preserve existing behavior for in-bounds cases
        f.setBounds(newX, newY, newWidth, newHeight);
    }

    if (didResize) {
        f.validate();
    }
}