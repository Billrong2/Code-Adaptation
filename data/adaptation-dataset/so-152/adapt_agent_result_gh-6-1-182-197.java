protected String encrypt(String value) {
    try {
        final byte[] plainBytes = value != null ? value.getBytes(UTF8) : new byte[0];

        final char[] specialCode = getSpecialCode();
        if (specialCode == null || specialCode.length == 0) {
            throw new IllegalStateException("Special code must not be null or empty");
        }

        final String androidId = getAndroidId();
        final byte[] salt = androidId != null ? androidId.getBytes(UTF8) : new byte[0];
        if (salt.length == 0) {
            throw new IllegalStateException("Derived salt must not be empty");
        }

        final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        final SecretKey key = keyFactory.generateSecret(new PBEKeySpec(specialCode));
        final Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
        pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(salt, 20));

        final byte[] encrypted = pbeCipher.doFinal(plainBytes);
        return new String(android.util.Base64.encode(encrypted, android.util.Base64.DEFAULT), UTF8);
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}