/**
     * Translates the specified byte array into a Base64 string.
     * <p>
     * This method is intended for internal use (e.g. by {@link #makeAcceptKey(String)})
     * and expects a non-null byte array.
     *
     * @param buf the byte array to encode (must not be {@code null})
     * @return the translated Base64 string (never {@code null})
     * @throws IllegalArgumentException if {@code buf} is {@code null}
     */
    private static String encodeBase64(byte[] buf) {
        if (buf == null) {
            throw new IllegalArgumentException("Input byte array must not be null");
        }

        int size = buf.length;
        char[] ar = new char[((size + 2) / 3) * 4];
        int a = 0;
        int i = 0;

        while (i < size) {
            byte b0 = buf[i++];
            byte b1 = (i < size) ? buf[i++] : 0;
            byte b2 = (i < size) ? buf[i++] : 0;

            int mask = 0x3F;
            ar[a++] = NanoWSD.ALPHABET[(b0 >> 2) & mask];
            ar[a++] = NanoWSD.ALPHABET[((b0 << 4) | ((b1 & 0xFF) >> 4)) & mask];
            ar[a++] = NanoWSD.ALPHABET[((b1 << 2) | ((b2 & 0xFF) >> 6)) & mask];
            ar[a++] = NanoWSD.ALPHABET[b2 & mask];
        }

        switch (size % 3) {
            case 1:
                ar[--a] = '=';
            case 2:
                ar[--a] = '=';
            default:
                break;
        }

        return new String(ar);
    }