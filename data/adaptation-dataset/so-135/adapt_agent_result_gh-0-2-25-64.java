public static String capitalizeEveryWord(final String source, java.util.List<Delimiter> delimiters, final java.util.Locale locale) {
        if (source == null) {
            return null;
        }

        char[] chars;

        if (delimiters == null || delimiters.isEmpty()) {
            delimiters = getDefaultDelimiters();
        }

        // If Locale specified, i18n toLowerCase is executed, to handle specific behaviors (eg. Turkish dotted and dotless 'i')
        if (locale != null) {
            chars = source.toLowerCase(locale).toCharArray();
        } else {
            chars = source.toLowerCase().toCharArray();
        }

        // First character ALWAYS capitalized, if it is a Letter.
        if (chars.length > 0 && Character.isLetter(chars[0]) && !isSurrogate(chars[0])) {
            chars[0] = Character.toUpperCase(chars[0]);
        }

        for (int i = 0; i < chars.length; i++) {
            if (!isSurrogate(chars[i]) && !Character.isLetter(chars[i])) {
                // Current char is not a Letter; check if it is a delimiter.
                for (Delimiter delimiter : delimiters) {
                    if (delimiter == null) {
                        continue;
                    }
                    if (delimiter.getDelimiter() == chars[i]) {
                        // Delimiter found, applying rules...
                        if (delimiter.capitalizeBefore() && i > 0
                                && Character.isLetter(chars[i - 1]) && !isSurrogate(chars[i - 1])) {
                            // previous character is a Letter and must be capitalized
                            chars[i - 1] = Character.toUpperCase(chars[i - 1]);
                        }
                        if (delimiter.capitalizeAfter() && i < chars.length - 1
                                && Character.isLetter(chars[i + 1]) && !isSurrogate(chars[i + 1])) {
                            // next character is a Letter and must be capitalized
                            chars[i + 1] = Character.toUpperCase(chars[i + 1]);
                        }
                        break;
                    }
                }
            }
        }
        return String.valueOf(chars);
    }