public static final String convertGlobToRegex(String pattern) {
    // Adapted from a StackOverflow answer on glob-to-regex conversion.
    // Original source: https://stackoverflow.com/questions/1247772/is-there-an-equivalent-of-java-util-regex-for-glob-type-patterns
    // Logic preserved; minor performance and robustness tweaks applied.

    if (pattern == null) {
      throw new IllegalArgumentException("pattern must not be null");
    }

    final StringBuilder sb = new StringBuilder(pattern.length() * 2);
    int inGroup = 0;
    int inClass = 0;
    int firstIndexInClass = -1;
    final char[] arr = pattern.toCharArray();

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
          if (inClass == 0)
            sb.append(".*");
          else
            sb.append('*');
          break;
        case '?':
          if (inClass == 0)
            sb.append('.');
          else
            sb.append('?');
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
          if (inClass == 0 || (firstIndexInClass == i && ch == '^'))
            sb.append('\\');
          sb.append(ch);
          break;
        case '!':
          if (firstIndexInClass == i)
            sb.append('^');
          else
            sb.append('!');
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
          if (inGroup > 0)
            sb.append('|');
          else
            sb.append(',');
          break;
        default:
          sb.append(ch);
      }
    }
    return sb.toString();
  }