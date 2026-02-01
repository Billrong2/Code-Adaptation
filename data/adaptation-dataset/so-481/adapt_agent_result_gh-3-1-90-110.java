private static int[] parseHexBinary(final String s) throws IllegalArgumentException {
        if (s == null) {
            throw new IllegalArgumentException("hexBinary cannot be null");
        }

        final int len = s.length();

        // "111" is not a valid hex encoding.
        if (len % 2 != 0) {
            throw new IllegalArgumentException("hexBinary needs to be even-length: " + s);
        }

        final int[] out = new int[len / 2];

        for (int i = 0; i < len; i += 2) {
            final int h = hexToBin(s.charAt(i));
            final int l = hexToBin(s.charAt(i + 1));
            if (h == -1 || l == -1) {
                throw new IllegalArgumentException("contains illegal character for hexBinary: " + s);
            }

            out[i / 2] = h * 16 + l;
        }

        return out;
    }