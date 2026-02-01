/**
 * Escapes HTML characters using numeric character references.
 * <p>
 * Adapted from a Stack Overflow answer.
 * </p>
 *
 * @param s the character sequence to escape
 * @return the escaped HTML string
 */
public static String escapeHtml(CharSequence s) {
    StringBuilder out = new StringBuilder(Math.max(16, s.length()));
    for (int i = 0; i < s.length(); i++) {
        char c = s.charAt(i);
        if (c > 127 || c == '"' || c == '<' || c == '>' || c == '&') {
            out.append("&#");
            out.append((int) c);
            out.append(';');
        } else {
            out.append(c);
        }
    }
    return out.toString();
}