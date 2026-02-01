public static @Nonnull String convertGlobToRegEx(@Nonnull String glob) {
    String line = glob.trim();
    if (line.isEmpty()) {
        return "";
    }
    int strLen = line.length();
    // Remove beginning and ending * globs because they're useless
    if (line.startsWith("*")) {
        line = line.substring(1);
        strLen--;
    }
    if (strLen > 0 && line.endsWith("*")) {
        line = line.substring(0, strLen - 1);
        strLen--;
    }
    StringBuilder regex = new StringBuilder(Math.max(0, strLen));
    boolean escaping = false;
    int inCurlies = 0;
    for (char currentChar : line.toCharArray()) {
        boolean wasEscaping = escaping;
        escaping = false; // reset once per iteration; only carried over by backslash
        if (!wasEscaping && currentChar == '\\') {
            escaping = true;
            continue; // apply escape to exactly the next character
        }
        switch (currentChar) {
            case '*':
                if (wasEscaping)
                    regex.append("\\*");
                else
                    regex.append(".*");
                break;
            case '?':
                if (wasEscaping)
                    regex.append("\\?");
                else
                    regex.append('.');
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
                regex.append('\\').append(currentChar);
                break;
            case '{':
                if (wasEscaping) {
                    regex.append("\\{");
                } else {
                    regex.append('(');
                    inCurlies++;
                }
                break;
            case '}':
                if (inCurlies > 0 && !wasEscaping) {
                    regex.append(')');
                    inCurlies--;
                } else if (wasEscaping) {
                    regex.append("\\}");
                } else {
                    regex.append('}');
                }
                break;
            case ',':
                if (inCurlies > 0 && !wasEscaping) {
                    regex.append('|');
                } else if (wasEscaping) {
                    regex.append("\\,");
                } else {
                    regex.append(',');
                }
                break;
            default:
                if (wasEscaping)
                    regex.append('\\');
                regex.append(currentChar);
        }
    }
    return regex.toString();
}