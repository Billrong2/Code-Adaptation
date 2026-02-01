/**
     * Generate a cryptographic hash for the given text using the specified algorithm and optional salt.
     * <p>
     * If {@code salt} is non-blank, it is appended to {@code text} prior to hashing. If the requested
     * algorithm is not available, this method returns {@code null}.
     * </p>
     *
     * @param text the input text to hash; if {@code null}, this method returns {@code null}
     * @param salt optional salt to append to the text before hashing
     * @param hashType the message digest algorithm (e.g., "MD5", "SHA1", "SHA-256")
     * @return the hex-encoded hash, or {@code null} if the algorithm is unavailable or input is invalid
     */
    public static String getHash(final String text, final String salt, final String hashType) {
        if (text == null || hashType == null) {
            return null;
        }

        final StringBuilder inputBuilder = new StringBuilder(text);
        if (org.apache.commons.lang.StringUtils.isNotBlank(salt)) {
            inputBuilder.append(salt);
        }

        try {
            final java.security.MessageDigest messageDigest = java.security.MessageDigest.getInstance(hashType);
            final byte[] digestBytes = messageDigest.digest(
                    inputBuilder.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));

            final StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < digestBytes.length; ++i) {
                hexString.append(Integer.toHexString((digestBytes[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return hexString.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            // TODO: add proper error handling or logging if required
        }
        return null;
    }