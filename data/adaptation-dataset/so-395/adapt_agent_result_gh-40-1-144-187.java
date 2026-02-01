public static boolean onTouchEvent(View v, MotionEvent event) {
    if (!(v instanceof TextView) || event == null) return false;

    TextView widget = (TextView) v;
    CharSequence text = widget.getText();
    if (!(text instanceof Spanned)) return false;

    Spanned spanned = (Spanned) text;
    int action = event.getAction();
    if (action != MotionEvent.ACTION_DOWN && action != MotionEvent.ACTION_UP) return false;

    Layout layout = widget.getLayout();
    if (layout == null) return false;

    int x = (int) event.getX();
    int y = (int) event.getY();

    x -= widget.getTotalPaddingLeft();
    y -= widget.getTotalPaddingTop();
    x += widget.getScrollX();
    y += widget.getScrollY();

    int line = layout.getLineForVertical(y);
    int off = layout.getOffsetForHorizontal(line, x);

    ClickableSpan[] links = spanned.getSpans(off, off, ClickableSpan.class);
    if (links == null || links.length == 0) return false;

    if (action == MotionEvent.ACTION_UP) {
        links[0].onClick(widget);
        return true;
    }

    // ACTION_DOWN: only highlight selection if text is actually Spannable
    if (action == MotionEvent.ACTION_DOWN && text instanceof Spannable) {
        Spannable spannable = (Spannable) text;
        Selection.setSelection(spannable,
                spannable.getSpanStart(links[0]),
                spannable.getSpanEnd(links[0]));
        return true;
    }

    return false;
}