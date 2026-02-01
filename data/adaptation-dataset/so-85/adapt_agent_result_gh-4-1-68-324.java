public final static String unescape_perl_string(final String oldstr) {

    /*
     * In contrast to fixing Java's broken regex charclasses,
     * this one need be no bigger, as unescaping shrinks the string
     * here, where in the other one, it grows it.
     */

    final StringBuffer newstr = new StringBuffer(oldstr.length());

    boolean saw_backslash = false;

    for (int i = 0; i < oldstr.length(); i++) {
        int cp = oldstr.codePointAt(i);
        if (cp > Character.MAX_VALUE) {
            i++; /****WE HATES UTF-16! WE HATES IT FOREVERSES!!!****/
        }

        if (!saw_backslash) {
            if (cp == '\\') {
                saw_backslash = true;
            } else {
                newstr.append(Character.toChars(cp));
            }
            continue;
        }

        if (cp == '\\') {
            saw_backslash = false;
            newstr.append('\\');
            newstr.append('\\');
            continue;
        }

        switch (cp) {

            case 'r':  newstr.append('\r'); break;
            case 'n':  newstr.append('\n'); break;
            case 'f':  newstr.append('\f'); break;

            /* PASS a \b THROUGH!! */
            case 'b':  newstr.append("\\b"); break;

            case 't':  newstr.append('\t'); break;
            case 'a':  newstr.append('\007'); break;
            case 'e':  newstr.append('\033'); break;

            case 'c': {
                if (++i == oldstr.length()) { die("trailing \\c"); }
                cp = oldstr.codePointAt(i);
                if (cp > 0x7f) { die("expected ASCII after \\c"); }
                newstr.append(Character.toChars(cp ^ 64));
                break;
            }

            case '8':
            case '9':
                die("illegal octal digit");
                break;

            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
                --i;
                /* FALLTHROUGH */

            case '0': {
                if (i + 1 == oldstr.length()) {
                    newstr.append(Character.toChars(0));
                    break;
                }
                i++;
                int digits = 0;
                int j;
                for (j = 0; j <= 2; j++) {
                    if (i + j == oldstr.length()) {
                        break;
                    }
                    int ch = oldstr.charAt(i + j);
                    if (ch < '0' || ch > '7') {
                        break;
                    }
                    digits++;
                }
                if (digits == 0) {
                    --i;
                    newstr.append('\0');
                    break;
                }
                int value;
                try {
                    value = Integer.parseInt(oldstr.substring(i, i + digits), 8);
                } catch (NumberFormatException nfe) {
                    die("invalid octal value for \\0 escape");
                    break;
                }
                newstr.append(Character.toChars(value));
                i += digits - 1;
                break;
            }

            case 'x': {
                if (i + 2 > oldstr.length()) {
                    die("string too short for \\x escape");
                }
                i++;
                boolean saw_brace = false;
                if (oldstr.charAt(i) == '{') {
                    i++;
                    saw_brace = true;
                }
                int j;
                for (j = 0; j < 8; j++) {
                    if (!saw_brace && j == 2) {
                        break;
                    }
                    int ch = oldstr.charAt(i + j);
                    if (ch > 127) {
                        die("illegal non-ASCII hex digit in \\x escape");
                    }
                    if (saw_brace && ch == '}') { break; }
                    if (!((ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F'))) {
                        die(String.format("illegal hex digit #%d '%c' in \\x", ch, ch));
                    }
                }
                if (j == 0) { die("empty braces in \\x{} escape"); }
                int value;
                try {
                    value = Integer.parseInt(oldstr.substring(i, i + j), 16);
                } catch (NumberFormatException nfe) {
                    die("invalid hex value for \\x escape");
                    break;
                }
                newstr.append(Character.toChars(value));
                if (saw_brace) { j++; }
                i += j - 1;
                break;
            }

            case 'u': {
                if (i + 4 > oldstr.length()) {
                    die("string too short for \\u escape");
                }
                i++;
                int j;
                for (j = 0; j < 4; j++) {
                    if (oldstr.charAt(i + j) > 127) {
                        die("illegal non-ASCII hex digit in \\u escape");
                    }
                }
                int value;
                try {
                    value = Integer.parseInt(oldstr.substring(i, i + j), 16);
                } catch (NumberFormatException nfe) {
                    die("invalid hex value for \\u escape");
                    break;
                }
                newstr.append(Character.toChars(value));
                i += j - 1;
                break;
            }

            case 'U': {
                if (i + 8 > oldstr.length()) {
                    die("string too short for \\U escape");
                }
                i++;
                int j;
                for (j = 0; j < 8; j++) {
                    if (oldstr.charAt(i + j) > 127) {
                        die("illegal non-ASCII hex digit in \\U escape");
                    }
                }
                int value;
                try {
                    value = Integer.parseInt(oldstr.substring(i, i + j), 16);
                } catch (NumberFormatException nfe) {
                    die("invalid hex value for \\U escape");
                    break;
                }
                newstr.append(Character.toChars(value));
                i += j - 1;
                break;
            }

            default:
                newstr.append('\\');
                newstr.append(Character.toChars(cp));
                break;
        }
        saw_backslash = false;
    }

    if (saw_backslash) {
        newstr.append('\\');
    }

    return newstr.toString();
}