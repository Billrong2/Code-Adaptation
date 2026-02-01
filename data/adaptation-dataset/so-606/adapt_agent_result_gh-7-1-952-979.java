    /**
     * Decodes the specified Base64 string into a byte array.
     *
     * @param base64 the Base64-encoded string (not null, length multiple of 4)
     * @return the decoded byte array
     * @throws IllegalArgumentException if the input is not valid Base64
     */
    public static byte[] decode(String base64) {
        if (base64 == null) {
            throw new IllegalArgumentException("Base64 input must not be null");
        }

        final int len = base64.length();
        if ((len & 0x03) != 0) {
            throw new IllegalArgumentException("Base64 input length must be a multiple of 4");
        }

        // count padding
        int padding = 0;
        if (len > 0 && base64.charAt(len - 1) == '=') {
            padding++;
            if (base64.charAt(len - 2) == '=') {
                padding++;
            }
        }

        final int outputLen = (len / 4) * 3 - padding;
        if (outputLen < 0) {
            throw new IllegalArgumentException("Invalid Base64 padding");
        }

        byte[] out = new byte[outputLen];
        int outIndex = 0;

        for (int inIndex = 0; inIndex < len; inIndex += 4) {
            char c0 = base64.charAt(inIndex);
            char c1 = base64.charAt(inIndex + 1);
            char c2 = base64.charAt(inIndex + 2);
            char c3 = base64.charAt(inIndex + 3);

            if (c0 >= toInt.length || c1 >= toInt.length ||
                (c2 != '=' && c2 >= toInt.length) ||
                (c3 != '=' && c3 >= toInt.length)) {
                throw new IllegalArgumentException("Invalid Base64 character");
            }

            int b0 = toInt[c0];
            int b1 = toInt[c1];
            int b2 = (c2 == '=') ? 0 : toInt[c2];
            int b3 = (c3 == '=') ? 0 : toInt[c3];

            int triple = (b0 << 18) | (b1 << 12) | (b2 << 6) | b3;

            if (outIndex < outputLen) {
                out[outIndex++] = (byte) ((triple >> 16) & 0xFF);
            } else {
                return out;
            }
            if (outIndex < outputLen) {
                out[outIndex++] = (byte) ((triple >> 8) & 0xFF);
            } else {
                return out;
            }
            if (outIndex < outputLen) {
                out[outIndex++] = (byte) (triple & 0xFF);
            } else {
                return out;
            }
        }

        return out;
    }