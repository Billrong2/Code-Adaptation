    /**
     * Encodes the given byte array into a Base64 string.
     * <p>
     * This method is used internally for generating the WebSocket
     * {@code Sec-WebSocket-Accept} header value during the handshake
     * (see {@link #makeAcceptKey(String)}).
     * </p>
     * <p>
     * Note: Modern Java platforms provide built-in Base64 encoders
     * (for example, {@code java.util.Base64}). This implementation is
     * retained for compatibility and historical reasons.
     * </p>
     * <p>
     * Algorithm adapted from a Stack Overflow community answer.
     * </p>
     *
     * @param buf the byte array to encode (must not be {@code null})
     * @return the encoded Base64 string (never {@code null})
     * @throws NullPointerException if {@code buf} is {@code null}
     */
    private static String encodeBase64(final byte[] buf) {
        java.util.Objects.requireNonNull(buf, "buf");

        final int size = buf.length;
        final char[] ar = new char[((size + 2) / 3) * 4];
        int a = 0;
        int i = 0;

        while (i < size) {
            final byte b0 = buf[i++];
            final byte b1 = (i < size) ? buf[i++] : 0;
            final byte b2 = (i < size) ? buf[i++] : 0;

            final int mask = 0x3F;
            ar[a++] = ALPHABET[(b0 >> 2) & mask];
            ar[a++] = ALPHABET[((b0 << 4) | ((b1 & 0xFF) >> 4)) & mask];
            ar[a++] = ALPHABET[((b1 << 2) | ((b2 & 0xFF) >> 6)) & mask];
            ar[a++] = ALPHABET[b2 & mask];
        }

        switch (size % 3) {
            case 1:
                ar[--a] = '=';
            case 2:
                ar[--a] = '=';
        }

        return new String(ar);
    }