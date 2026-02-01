    // Adapted from StackOverflow: https://stackoverflow.com/a/26318757
    public static boolean isFullyQualifiedClassname(final String classname) {
        if (classname == null) return false;

        String[] parts = classname.split("[\\.]");
        if (parts.length == 0) return false;

        for (String part : parts) {
            CharacterIterator iter = new StringCharacterIterator(part);

            // Check first character (there should at least be one character for each part)
            char c = iter.first();
            if (c == CharacterIterator.DONE) return false;
            if (!Character.isJavaIdentifierStart(c) && !Character.isIdentifierIgnorable(c))
                return false;

            // Check the remaining characters, if there are any
            c = iter.next();
            while (c != CharacterIterator.DONE) {
                if (!Character.isJavaIdentifierPart(c) && !Character.isIdentifierIgnorable(c))
                    return false;
                c = iter.next();
            }
        }

        return true;
    }