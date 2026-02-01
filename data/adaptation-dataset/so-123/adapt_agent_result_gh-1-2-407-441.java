    /**
     * Truncates a string so that its UTF-8 encoded byte length does not exceed the given limit.
     * <p>
     * Adapted from a Stack Overflow answer (SO ID: TODO).
     * </p>
     */
    private static String truncateWhenUTF8(final String s, final int maxBytes)
    {
        int bytesUsed = 0;

        for (int i = 0; i < s.length(); i++)
        {
            final char c = s.charAt(i);

            // Ranges from http://en.wikipedia.org/wiki/UTF-8
            int skip = 0;
            final int more;

            if (c <= 0x007f)
            {
                more = 1;
            }
            else if (c <= 0x07FF)
            {
                more = 2;
            }
            else if (c <= 0xd7ff)
            {
                more = 3;
            }
            else if (c <= 0xDFFF)
            {
                // Surrogate area, consume next char as well
                more = 4;
                skip = 1;
            }
            else
            {
                more = 3;
            }

            if (bytesUsed + more > maxBytes)
            {
                return s.substring(0, i);
            }

            bytesUsed += more;
            i += skip;
        }

        return s;
    }