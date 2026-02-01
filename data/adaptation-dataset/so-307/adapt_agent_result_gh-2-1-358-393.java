public static String[] splitSpacesAndQuotes(String str, boolean retainOuterQuotes) {
    final char SPACE = ' ';
    final char SINGLE_QUOTE = '\'';
    final char DOUBLE_QUOTE = '"';

    if (str == null) {
        return new String[0];
    }
    String input = str.trim();
    if (input.isEmpty()) {
        return new String[0];
    }

    // trailing-space technique to ensure last token is flushed
    input = input + SPACE;

    java.util.ArrayList<String> tokens = new java.util.ArrayList<String>();
    StringBuilder currentToken = new StringBuilder();
    char activeQuote = 0; // 0 = not in quoted section, otherwise holds the quote char (' or ")

    for (int i = 0; i < input.length(); i++) {
        char c = input.charAt(i);

        // handle quote characters
        if (c == SINGLE_QUOTE || c == DOUBLE_QUOTE) {
            if (activeQuote == 0) {
                // opening a quoted section
                activeQuote = c;
                if (retainOuterQuotes) {
                    currentToken.append(c);
                }
            } else if (activeQuote == c) {
                // closing the active quoted section
                if (retainOuterQuotes) {
                    currentToken.append(c);
                }
                activeQuote = 0;
            } else {
                // quote inside a differently quoted section is literal
                currentToken.append(c);
            }
            continue;
        }

        // handle space as delimiter only when not inside quotes
        if (c == SPACE && activeQuote == 0) {
            if (currentToken.length() > 0) {
                tokens.add(currentToken.toString());
                currentToken.setLength(0);
            }
            continue;
        }

        // regular character
        currentToken.append(c);
    }

    // tolerate unmatched quotes by emitting whatever was accumulated
    if (currentToken.length() > 0) {
        tokens.add(currentToken.toString());
    }

    return tokens.toArray(new String[tokens.size()]);
}