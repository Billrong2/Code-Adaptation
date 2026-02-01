public String decrypt(String value, Context context) {
        if (value == null) {
            return null;
        }
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null");
        }
        try {
            final byte[] decodedBytes = Base64.decode(value, Base64.DEFAULT);
            final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            final SecretKey key = keyFactory.generateSecret(new PBEKeySpec(mSerkit));
            final Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
            final byte[] salt = Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID
            ).getBytes(java.nio.charset.StandardCharsets.UTF_8);
            pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(salt, 20));
            final byte[] plaintextBytes = pbeCipher.doFinal(decodedBytes);
            return new String(plaintextBytes, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }