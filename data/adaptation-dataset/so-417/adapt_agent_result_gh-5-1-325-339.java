private void stripUnderlines(final TextView textView) {
        // Replace URLSpan with URLSpanNoUnderline to remove default underline styling
        if (textView == null) {
            return;
        }
        final CharSequence text = textView.getText();
        if (text == null || !(text instanceof Spannable)) {
            // Safe fallback: nothing to do if the text is not spannable
            return;
        }

        final Spannable spannable = (Spannable) text;
        final URLSpan[] spans = spannable.getSpans(0, spannable.length(), URLSpan.class);
        for (URLSpan oldSpan : spans) {
            final int start = spannable.getSpanStart(oldSpan);
            final int end = spannable.getSpanEnd(oldSpan);
            spannable.removeSpan(oldSpan);
            final URLSpan newSpan = new URLSpanNoUnderline(oldSpan.getURL());
            spannable.setSpan(newSpan, start, end, 0);
        }
        textView.setText(spannable);
    }