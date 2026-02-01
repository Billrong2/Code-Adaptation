public static void stripUnderlines(final TextView textView) {
        if (textView == null) {
            return;
        }
        final CharSequence text = textView.getText();
        if (!(text instanceof Spannable)) {
            // Assumption: caller provides a TextView whose text is already Spannable; otherwise no-op
            return;
        }
        final Spannable spannable = (Spannable) text;
        final URLSpan[] spans = spannable.getSpans(0, spannable.length(), URLSpan.class);
        for (URLSpan span : spans) {
            final int start = spannable.getSpanStart(span);
            final int end = spannable.getSpanEnd(span);
            if (start >= 0 && end >= start) {
                spannable.removeSpan(span);
                URLSpan replacement = new URLSpanNoUnderline(span.getURL());
                spannable.setSpan(replacement, start, end, 0);
            }
        }
    }