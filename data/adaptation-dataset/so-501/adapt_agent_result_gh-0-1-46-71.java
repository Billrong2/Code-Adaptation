public static String replace(String pattern, String subject, int limit, AtomicInteger count, Callback callback) {
    StringBuffer sb = new StringBuffer();
    Matcher matcher = Pattern.compile(pattern).matcher(subject);
    int i;
    for (i = 0; (limit < 0 || i < limit) && matcher.find(); i++) {
        String replacement = callback.matchFound(matcher.toMatchResult());
        replacement = Matcher.quoteReplacement(replacement); // probably what you want...
        matcher.appendReplacement(sb, replacement);
    }
    matcher.appendTail(sb);

    if (count != null) {
        count.set(i);
    }
    return sb.toString();
}