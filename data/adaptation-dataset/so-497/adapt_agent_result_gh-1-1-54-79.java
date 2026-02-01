public static boolean addSmiles(final Context context, final Spannable spannable) {
    if (context == null || spannable == null || emoticons == null) {
        return false;
    }

    boolean hasChanges = false;

    for (java.util.Map.Entry<Pattern, Integer> entry : emoticons.entrySet()) {
        final Matcher matcher = entry.getKey().matcher(spannable);
        while (matcher.find()) {
            boolean shouldSetSpan = true;
            for (ImageSpan span : spannable.getSpans(matcher.start(), matcher.end(), ImageSpan.class)) {
                if (spannable.getSpanStart(span) >= matcher.start()
                        && spannable.getSpanEnd(span) <= matcher.end()) {
                    spannable.removeSpan(span);
                } else {
                    shouldSetSpan = false;
                    break;
                }
            }
            if (shouldSetSpan) {
                hasChanges = true;
                spannable.setSpan(
                        new ImageSpan(context, entry.getValue()),
                        matcher.start(),
                        matcher.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            }
        }
    }

    return hasChanges;
}