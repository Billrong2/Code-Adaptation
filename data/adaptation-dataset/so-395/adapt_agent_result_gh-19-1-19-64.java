@Override
public boolean onTouchEvent(MotionEvent event) {
    boolean handledLink = false;

    if (event != null) {
        Object text = getText();
        if (text instanceof Spanned) {
            Layout layout = getLayout();
            if (layout != null) {
                Spannable buffer = (Spannable) text;
                int action = event.getAction();

                if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
                    int x = (int) event.getX();
                    int y = (int) event.getY();

                    x -= getTotalPaddingLeft();
                    y -= getTotalPaddingTop();
                    x += getScrollX();
                    y += getScrollY();

                    if (y >= 0 && y <= layout.getHeight()) {
                        int line = layout.getLineForVertical(y);
                        int offset = layout.getOffsetForHorizontal(line, x);

                        if (offset >= 0 && offset <= buffer.length()) {
                            ClickableSpan[] links = buffer.getSpans(offset, offset, ClickableSpan.class);
                            if (links != null && links.length > 0) {
                                if (action == MotionEvent.ACTION_UP) {
                                    links[0].onClick(this);
                                } else {
                                    Selection.setSelection(
                                            buffer,
                                            buffer.getSpanStart(links[0]),
                                            buffer.getSpanEnd(links[0])
                                    );
                                }
                                handledLink = true;
                            }
                        }
                    }
                }
            }
        }
    }

    return handledLink || super.onTouchEvent(event);
}