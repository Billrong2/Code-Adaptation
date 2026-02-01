private TouchableUrlSpan getTouchedSpan(TextView textView, Spannable spannable, MotionEvent event) {
        if (textView == null || spannable == null || event == null) return null;
        final Layout layout = textView.getLayout();
        if (layout == null) return null;

        // Adjust touch coordinates for padding and scroll
        int x = (int) event.getX();
        int y = (int) event.getY();
        x -= textView.getTotalPaddingLeft();
        y -= textView.getTotalPaddingTop();
        x += textView.getScrollX();
        y += textView.getScrollY();

        // Map coordinates to line and character offset
        int line = layout.getLineForVertical(y);
        if (line < 0 || line >= layout.getLineCount()) return null;
        int offset = layout.getOffsetForHorizontal(line, x);
        if (offset < 0 || offset >= spannable.length()) return null;

        // Find the first TouchableUrlSpan at the offset
        TouchableUrlSpan[] spans = spannable.getSpans(offset, offset, TouchableUrlSpan.class);
        if (spans == null || spans.length == 0) return null;
        return spans[0];
    }