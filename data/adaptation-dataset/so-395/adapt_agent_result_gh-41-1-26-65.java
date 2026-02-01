@Override
public boolean onTouchEvent(MotionEvent event) {
    // Only handle touch when text is truly Spannable (required for Selection operations)
    CharSequence text = getText();
    if (!(text instanceof Spannable)) {
        return false;
    }

    Layout layout = getLayout();
    if (layout == null) {
        return false;
    }

    final int action = event.getAction();
    if (action != MotionEvent.ACTION_DOWN && action != MotionEvent.ACTION_UP) {
        return false;
    }

    // Translate touch coordinates into text layout coordinates
    int x = (int) event.getX();
    int y = (int) event.getY();

    x -= getTotalPaddingLeft();
    y -= getTotalPaddingTop();
    x += getScrollX();
    y += getScrollY();

    // Ensure y maps to a valid line
    int lineCount = layout.getLineCount();
    if (lineCount == 0) {
        return false;
    }

    int line = layout.getLineForVertical(y);
    if (line < 0 || line >= lineCount) {
        return false;
    }

    int offset = layout.getOffsetForHorizontal(line, x);
    Spannable buffer = (Spannable) text;

    ClickableSpan[] links = buffer.getSpans(offset, offset, ClickableSpan.class);
    if (links == null || links.length == 0) {
        return false;
    }

    ClickableSpan link = links[0];

    if (action == MotionEvent.ACTION_DOWN) {
        // Highlight/select on DOWN and consume the event
        Selection.setSelection(buffer,
                buffer.getSpanStart(link),
                buffer.getSpanEnd(link));
        return true;
    } else if (action == MotionEvent.ACTION_UP) {
        // Invoke click on UP but do NOT consume, allowing parent handlers to receive it
        link.onClick(this);
        return false;
    }

    return false;
}