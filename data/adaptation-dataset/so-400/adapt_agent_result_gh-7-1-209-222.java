protected String decrypt(String value) {

        try {
            // Preserve null-handling: map null input to empty byte array
            final byte[] ciphertext = value != null
                    ? android.util.Base64.decode(value, android.util.Base64.DEFAULT)
                    : new byte[0];

            // Validate required inputs
            if (context == null) {
                throw new IllegalStateException("Context must not be null");
            }
            final char[] specialCode = getSpecialCode();
            if (specialCode == null) {
                throw new IllegalStateException("Special code must not be null");
            }
            final String androidId = getAndroidId();
            if (androidId == null) {
                throw new IllegalStateException("Android ID must not be null");
            }

            final javax.crypto.SecretKeyFactory keyFactory = javax.crypto.SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            final javax.crypto.SecretKey key = keyFactory.generateSecret(new javax.crypto.spec.PBEKeySpec(specialCode));
            final javax.crypto.Cipher pbeCipher = javax.crypto.Cipher.getInstance("PBEWithMD5AndDES");
            pbeCipher.init(javax.crypto.Cipher.DECRYPT_MODE, key,
                    new javax.crypto.spec.PBEParameterSpec(androidId.getBytes(UTF8), 20));

            final byte[] plaintextBytes = pbeCipher.doFinal(ciphertext);
            return new String(plaintextBytes, UTF8);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }