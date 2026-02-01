private static String replaceTokens(String template, java.util.Map<String, String> variables) {
    if (template == null || variables == null) {
        return template;
    }
    final java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("_(.+?)_");
    final java.util.regex.Matcher matcher = pattern.matcher(template);
    final StringBuffer buffer = new StringBuffer();
    while (matcher.find()) {
        final String tokenName = matcher.group(1);
        final String replacement = variables.get(tokenName);
        // remove the matched token first
        matcher.appendReplacement(buffer, "");
        if (replacement != null) {
            // append raw replacement without regex escaping
            buffer.append(replacement);
        } else {
            // leave the original token unchanged
            buffer.append(matcher.group(0));
        }
    }
    matcher.appendTail(buffer);
    return buffer.toString();
}