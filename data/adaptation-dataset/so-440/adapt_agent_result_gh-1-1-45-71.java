public static String encode(final String s, final String encoding) throws UnsupportedEncodingException {
        if (s == null) {
            throw new IllegalArgumentException("Input string must not be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Encoding must not be null");
        }

        final byte[] bytes = s.getBytes(encoding);
        final StringBuilder sb = new StringBuilder(bytes.length << 1);
        final char[] digits = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
        // RFC 5987 attr-char whitelist (must remain sorted for binarySearch)
        final byte[] attrChar = {
            '!','#','$','&','+','-','.','0','1','2','3','4','5','6','7','8','9',
            'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
            '^','_','`','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','|','~'
        };

        for (final byte b : bytes) {
            if (Arrays.binarySearch(attrChar, b) >= 0) {
                sb.append((char) b);
            } else {
                sb.append('%');
                sb.append(digits[(b >>> 4) & 0x0f]);
                sb.append(digits[b & 0x0f]);
            }
        }
        return sb.toString();
    }