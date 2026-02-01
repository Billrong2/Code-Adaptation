protected String encrypt(String value) {
    try {
        if (secret == null) {
            throw new IllegalStateException("Secret must not be null");
        }
        final byte[] salt = getSalt();
        if (salt == null) {
            throw new IllegalStateException("Salt must not be null");
        }
        final byte[] bytes = value != null ? value.getBytes(UTF8) : new byte[0];
        final javax.crypto.SecretKeyFactory keyFactory = javax.crypto.SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        final javax.crypto.SecretKey key = keyFactory.generateSecret(new javax.crypto.spec.PBEKeySpec(secret));
        final javax.crypto.Cipher pbeCipher = javax.crypto.Cipher.getInstance("PBEWithMD5AndDES");
        pbeCipher.init(javax.crypto.Cipher.ENCRYPT_MODE, key, new javax.crypto.spec.PBEParameterSpec(salt, 20));
        return new String(android.util.Base64.encode(pbeCipher.doFinal(bytes), android.util.Base64.NO_WRAP), UTF8);
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}