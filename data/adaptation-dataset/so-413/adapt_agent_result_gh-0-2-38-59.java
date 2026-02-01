@Override
protected boolean setFrame(int l, int t, int r, int b) {
    // Derive view dimensions from frame bounds
    final int viewWidth = r - l;
    final int viewHeight = b - t;

    // Guard against null drawable and invalid dimensions
    if (getDrawable() != null && viewWidth > 0 && viewHeight > 0) {
        final int intrinsicWidth = getDrawable().getIntrinsicWidth();
        final int intrinsicHeight = getDrawable().getIntrinsicHeight();

        if (intrinsicWidth > 0 && intrinsicHeight > 0) {
            final Matrix matrix = getImageMatrix();

            final float scaleFactorWidth = (float) viewWidth / (float) intrinsicWidth;
            final float scaleFactorHeight = (float) viewHeight / (float) intrinsicHeight;
            final float scaleFactor = Math.max(scaleFactorWidth, scaleFactorHeight);

            // Scale from top-left corner
            matrix.setScale(scaleFactor, scaleFactor, 0f, 0f);
            setImageMatrix(matrix);
        }
    }

    return super.setFrame(l, t, r, b);
}