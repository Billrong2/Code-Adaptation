public String decrypt(Context context, String value) {
        if (value == null) {
            return null;
        }

        final String algorithm = "PBEWithMD5AndDES";
        final int iterationCount = 20;

        try {
            final byte[] ciphertext;
            try {
                ciphertext = Base64.decode(value, Base64.DEFAULT);
            } catch (IllegalArgumentException invalidBase64) {
                return null;
            }

            final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm);
            final SecretKey key = keyFactory.generateSecret(new PBEKeySpec(mSerkit));
            final Cipher pbeCipher = Cipher.getInstance(algorithm);

            final String androidId = Settings.Secure.getString(
                    context.getContentResolver(), Settings.Secure.ANDROID_ID);
            final byte[] salt = androidId != null
                    ? androidId.getBytes(java.nio.charset.StandardCharsets.UTF_8)
                    : new byte[0];

            pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(salt, iterationCount));
            final byte[] plaintextBytes = pbeCipher.doFinal(ciphertext);
            return new String(plaintextBytes, java.nio.charset.StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new RuntimeException("Error while decrypting value", e);
        }
    }