/**
     * Converts a glob pattern to an equivalent regular expression string.
     * <p>
     * Based on a well-known Stack Overflow implementation for glob-to-regex conversion.
     * </p>
     *
     * @param str the glob pattern to convert
     * @return a regular expression representing the given glob pattern
     * @throws IllegalArgumentException if {@code str} is {@code null}
     */
    public static String convertGlobToRegex(final String str) {
        if (str == null) {
            throw new IllegalArgumentException("Input glob string must not be null");
        }

        String line = str.trim();
        int strLen = line.length();
        final StringBuilder sb = new StringBuilder(strLen);

        // Remove beginning and ending * globs because they're useless
        if (line.startsWith("*")) {
            line = line.substring(1);
            strLen--;
        }
        if (line.endsWith("*")) {
            line = line.substring(0, strLen - 1);
            strLen--;
        }

        boolean escaping = false;
        int inCurlies = 0;
        for (char currentChar : line.toCharArray()) {
            switch (currentChar) {
                case '*':
                    if (escaping)
                        sb.append("\\*");
                    else
                        sb.append(".*");
                    escaping = false;
                    break;
                case '?':
                    if (escaping)
                        sb.append("\\?");
                    else
                        sb.append('.');
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