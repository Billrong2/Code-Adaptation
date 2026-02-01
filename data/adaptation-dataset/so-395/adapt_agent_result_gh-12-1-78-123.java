public boolean onTouch(android.view.View v, android.view.MotionEvent event) {
    if (v == null || event == null) {
        return false;
    }
    if (!(v instanceof android.widget.TextView)) {
        return false;
    }

    final android.widget.TextView widget = (android.widget.TextView) v;
    final java.lang.CharSequence text = widget.getText();
    if (!(text instanceof android.text.Spanned)) {
        return false;
    }

    final android.text.Layout layout = widget.getLayout();
    if (layout == null) {
        return false;
    }

    final android.text.Spannable buffer = (android.text.Spannable) text;
    final int action = event.getAction();

    if (action == android.view.MotionEvent.ACTION_UP
            || action == android.view.MotionEvent.ACTION_DOWN) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        x -= widget.getTotalPaddingLeft();
        y -= widget.getTotalPaddingTop();

        x += widget.getScrollX();
        y += widget.getScrollY();

        final int line = layout.getLineForVertical(y);
        final int off = layout.getOffsetForHorizontal(line, x);

        final android.text.style.ClickableSpan[] link = buffer.getSpans(off, off,
                android.text.style.ClickableSpan.class);

        if (link != null && link.length > 0) {
            if (action == android.view.MotionEvent.ACTION_UP) {
                link[0].onClick(widget);
            } else {
                android.text.Selection.setSelection(buffer,
                        buffer.getSpanStart(link[0]),
                        buffer.getSpanEnd(link[0]));
            }
            return true;
        }
    }

    return false;
}