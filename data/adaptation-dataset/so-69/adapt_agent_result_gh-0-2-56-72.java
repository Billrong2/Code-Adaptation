private static <A extends CharacterStyle, B extends CharacterStyle> Spannable replaceAll(final CharSequence original, final Class<A> sourceType, final SpanConverter<A, B> converter) {
        if (original == null) {
            return null;
        }
        if (sourceType == null || converter == null) {
            return new SpannableString(original);
        }

        final SpannableString result = new SpannableString(original);

        if (!(original instanceof android.text.Spanned)) {
            return result;
        }

        A[] spans = result.getSpans(0, result.length(), sourceType);
        if (spans == null || spans.length == 0) {
            return result;
        }

        for (A span : spans) {
            int start = result.getSpanStart(span);
            int end = result.getSpanEnd(span);
            int flags = result.getSpanFlags(span);

            result.removeSpan(span);
            B converted = converter.convert(span);
            if (converted != null && start >= 0 && end >= start) {
                result.setSpan(converted, start, end, flags);
            }
        }

        return result;
    }