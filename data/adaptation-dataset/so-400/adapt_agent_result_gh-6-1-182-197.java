protected String encrypt(String value) {
    try {
        final byte[] bytes = value != null ? value.getBytes(UTF8) : new byte[0];

        final char[] specialCode = getSpecialCode();
        if (specialCode == null) {
            throw new IllegalStateException("getSpecialCode() returned null");
        }

        final String androidId = getAndroidId();
        if (androidId == null) {
            throw new IllegalStateException("getAndroidId() returned null");
        }

        final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        final SecretKey key = keyFactory.generateSecret(new PBEKeySpec(specialCode));
        final Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
        pbeCipher.init(Cipher.ENCRYPT_MODE, key,
                new PBEParameterSpec(androidId.getBytes(UTF8), 20));

        return new String(
                android.util.Base64.encode(pbeCipher.doFinal(bytes), android.util.Base64.NO_WRAP),
                UTF8);

    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}