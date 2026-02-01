protected String encrypt(String value) {
    try {
        // validate salt
        if (SALT == null || SALT.length < 8) {
            throw new IllegalStateException("SALT must be predefined and have sufficient length");
        }
        final byte[] plainBytes = value != null ? value.getBytes(UTF8) : new byte[0];
        final javax.crypto.SecretKeyFactory keyFactory = javax.crypto.SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        final javax.crypto.SecretKey key = keyFactory.generateSecret(new javax.crypto.spec.PBEKeySpec(SEKRIT));
        final javax.crypto.Cipher pbeCipher = javax.crypto.Cipher.getInstance("PBEWithMD5AndDES");
        pbeCipher.init(javax.crypto.Cipher.ENCRYPT_MODE, key, new javax.crypto.spec.PBEParameterSpec(SALT, 20));
        final byte[] encrypted = pbeCipher.doFinal(plainBytes);
        return new String(com.worxforus.Base64Support.encode(encrypted, com.worxforus.Base64Support.DEFAULT), UTF8);
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}