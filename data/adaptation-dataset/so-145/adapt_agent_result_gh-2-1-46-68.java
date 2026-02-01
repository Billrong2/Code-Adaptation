private NoteBlockClickableSpan getSpanUnderTouch(final TextView textView, final Spannable spannable, final MotionEvent event) {
    if (textView == null || spannable == null || event == null) {
        return null;
    }

    final Layout layout = textView.getLayout();
    if (layout == null) {
        return null;
    }

    final int x = (int) event.getX() - textView.getTotalPaddingLeft() + textView.getScrollX();
    final int y = (int) event.getY() - textView.getTotalPaddingTop() + textView.getScrollY();

    if (x < 0 || y < 0) {
        return null;
    }

    final int line = layout.getLineForVertical(y);
    if (line < 0 || line >= layout.getLineCount()) {
        return null;
    }

    final int offset = layout.getOffsetForHorizontal(line, x);
    if (offset < 0 || offset >= spannable.length()) {
        return null;
    }

    final NoteBlockClickableSpan[] spans = spannable.getSpans(offset, offset, NoteBlockClickableSpan.class);
    if (spans == null || spans.length == 0) {
        return null;
    }

    return spans[0];
}