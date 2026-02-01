public static String globToRegex(final String line) {
    if (line == null) {
        return "";
    }
    String trimmed = line.trim();
    int strLen = trimmed.length();
    StringBuilder sb = new StringBuilder(strLen);
    boolean escaping = false;
    int inCurlies = 0;
    for (char currentChar : trimmed.toCharArray()) {
        switch (currentChar) {
        case '*':
            if (escaping) {
                sb.append("\\*");
            } else {
                sb.append(".*");
            }
            escaping = false;
            break;
        case '?':
            if (escaping) {
                sb.append("\\?");
            } else {
                sb.append('.');
            }
            escaping = false;
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
            sb.append('\\');
            sb.append(currentChar);
            escaping = false;
            break;
        case '\\':
            if (escaping) {
                sb.append("\\\\");
                escaping = false;
            } else {
                escaping = true;
            }
            break;
        case '{':
            if (escaping) {
                sb.append("\\{");
            } else {
                sb.append('(');
                inCurlies++;
            }
            escaping = false;
            break;
        case '}':
            if (inCurlies > 0 && !escaping) {
                sb.append(')');
                inCurlies--;
            } else if (escaping) {
                sb.append("\\}");
            } else {
                sb.append("}");
            }
            escaping = false;
            break;
        case ',':
            if (inCurlies > 0 && !escaping) {
                sb.append('|');
            } else if (escaping) {
                sb.append("\\,");
            } else {
                sb.append(",");
            }
            break;
        default:
            escaping = false;
            sb.append(currentChar);
        }
    }
    return sb.toString();
}