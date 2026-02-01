/**
 * Converts a standard POSIX Shell globbing pattern into a regular expression
 * pattern. The result can be used with the standard {@link java.util.regex} API to
 * recognize strings which match the glob pattern.
 * <p/>
 * When {@code matchWholeString} is {@code true}, the generated regular expression
 * is anchored at the start (^) and end ($) so that it matches the entire input.
 * When {@code false}, the behavior matches the original implementation and the
 * regex may match a substring.
 * <p/>
 * See also, the POSIX Shell language:
 * http://pubs.opengroup.org/onlinepubs/009695399/utilities/xcu_chap02.html#tag_02_13_01
 *
 * @param globPattern A glob pattern.
 * @param matchWholeString whether the resulting regex should match the entire input
 * @return A regex pattern string corresponding to the given glob pattern.
 * @throws IllegalArgumentException if {@code globPattern} is null
 */
public static final String convertGlobToRegex(final String globPattern, final boolean matchWholeString) {
    if (globPattern == null) {
        throw new IllegalArgumentException("Glob pattern must not be null");
    }

    final StringBuilder sb = new StringBuilder(globPattern.length());
    int inGroup = 0;
    int inClass = 0;
    int firstIndexInClass = -1;
    final char[] arr = globPattern.toCharArray();

    for (int i = 0; i < arr.length; i++) {
        final char ch = arr[i];
        switch (ch) {
            case '\\':
                if (++i >= arr.length) {
                    sb.append('\\');
                } else {
                    final char next = arr[i];
                    switch (next) {
                        case ',':
                            // escape not needed
                            break;
                        case 'Q':
                        case 'E':
                            // extra escape needed
                            sb.append('\\');
                        default:
                            sb.append('\\');
                    }
                    sb.append(next);
                }
                break;
            case '*':
                if (inClass == 0) {
                    sb.append(".*");
                } else {
                    sb.append('*');
                }
                break;
            case '?':
                if (inClass == 0) {
                    sb.append('.');
                } else {
                    sb.append('?');
                }
                break;
            case '[':
                inClass++;
                firstIndexInClass = i + 1;
                sb.append('[');
                break;
            case ']':
                inClass--;
                sb.append(']');
                break;
            case '.':
            case '(':
            case ')':
            case '+':
            case '|':
            case '^':
            case '$':
            case '@':
            case '%':
                if (inClass == 0 || (firstIndexInClass == i && ch == '^')) {
                    sb.append('\\');
                }
                sb.append(ch);
                break;
            case '!':
                if (firstIndexInClass == i) {
                    sb.append('^');
                } else {
                    sb.append('!');
                }
                break;
            case '{':
                inGroup++;
                sb.append('(');
                break;
            case '}':
                inGroup--;
                sb.append(')');
                break;
            case ',':
                if (inGroup > 0) {
                    sb.append('|');
                } else {
                    sb.append(',');
                }
                break;
            default:
                sb.append(ch);
        }
    }

    final String coreRegex = sb.toString();
    return matchWholeString ? "^" + coreRegex + "$" : coreRegex;
}