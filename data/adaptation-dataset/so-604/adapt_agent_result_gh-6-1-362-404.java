    /**
     * <p>
     * Encrypt a plain text string using TripleDES and return the Base64-encoded ciphertext.
     * </p>
     * <p>
     * The algorithm used is <code>base64(TripleDES/CBC/PKCS5Padding(plainText))</code> with a zero IV.
     * A 24-byte 3DES key is derived from the MD5 digest of the provided password bytes.
     * </p>
     *
     * @param plainText
     *            text to encrypt (UTF-8)
     * @param password
     *            password bytes used to derive the encryption key
     * @return Base64-encoded encrypted text
     * @throws ChiliLogException
     *             if any encryption error occurs
     */
    public static String encryptTripleDES(String plainText, byte[] password) throws ChiliLogException {
        try {
            if (plainText == null) {
                throw new org.apache.commons.lang.NullArgumentException("plainText");
            }
            if (password == null || password.length == 0) {
                throw new org.apache.commons.lang.NullArgumentException("password");
            }

            final int TRIPLE_DES_KEY_LENGTH = 24;
            final int DES_BLOCK_SIZE = 8;

            final java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            final byte[] digestOfPassword = md.digest(password);
            final byte[] keyBytes = java.util.Arrays.copyOf(digestOfPassword, TRIPLE_DES_KEY_LENGTH);
            for (int j = 0, k = 16; j < 8;) {
                keyBytes[k++] = keyBytes[j++];
            }

            final javax.crypto.SecretKey key = new javax.crypto.spec.SecretKeySpec(keyBytes, "DESede");
            final javax.crypto.spec.IvParameterSpec iv = new javax.crypto.spec.IvParameterSpec(new byte[DES_BLOCK_SIZE]);
            final javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("DESede/CBC/PKCS5Padding");
            cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, key, iv);

            final byte[] plainTextBytes = plainText.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            final byte[] cipherText = cipher.doFinal(plainTextBytes);

            final org.apache.commons.codec.binary.Base64 encoder = new org.apache.commons.codec.binary.Base64(1000, new byte[] {}, false);
            return encoder.encodeToString(cipherText);
        } catch (Exception ex) {
            throw new ChiliLogException(ex, "Error attempting to encrypt using TripleDES. " + ex.getMessage());
        }
    }