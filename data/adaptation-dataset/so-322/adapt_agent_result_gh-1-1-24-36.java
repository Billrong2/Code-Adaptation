@Override
public boolean onTouch(final View v, final MotionEvent event) {
    if (v == null || event == null) {
        return false;
    }

    if (event.getAction() == MotionEvent.ACTION_DOWN && drawable != null) {
        final Rect bounds = drawable.getBounds();
        if (bounds != null && !bounds.isEmpty()) {
            final int x = (int) event.getX();
            final int y = (int) event.getY();
            final int viewRight = v.getRight();
            final int paddingRight = v.getPaddingRight();
            final int paddingTop = v.getPaddingTop();
            final int paddingBottom = v.getPaddingBottom();
            final int height = v.getHeight();

            if (x >= (viewRight - bounds.width() - fuzz)
                    && x <= (viewRight - paddingRight + fuzz)
                    && y >= (paddingTop - fuzz)
                    && y <= (height - paddingBottom + fuzz)) {
                return onDrawableTouch(event);
            }
        }
    }
    return false;
}