private CustomClickableSpan getPressedSpan(final TextView textView, final Spannable spannable, final MotionEvent event) {
    if (textView == null || spannable == null || event == null) {
        return null;
    }

    final android.text.Layout layout = textView.getLayout();
    if (layout == null) {
        return null;
    }

    // Adjust touch coordinates to text content coordinates
    int x = (int) event.getX();
    int y = (int) event.getY();

    x -= textView.getTotalPaddingLeft();
    y -= textView.getTotalPaddingTop();

    x += textView.getScrollX();
    y += textView.getScrollY();

    if (x < 0 || y < 0) {
        return null;
    }

    final int line = layout.getLineForVertical(y);
    if (line < 0 || line >= layout.getLineCount()) {
        return null;
    }

    final int offset = layout.getOffsetForHorizontal(line, x);
    if (offset < 0 || offset > spannable.length()) {
        return null;
    }

    final CustomClickableSpan[] spans = spannable.getSpans(offset, offset, CustomClickableSpan.class);
    if (spans != null && spans.length > 0) {
        return spans[0];
    }

    return null;
}