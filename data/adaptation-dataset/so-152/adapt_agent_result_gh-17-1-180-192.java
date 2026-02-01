protected String decrypt(String ciphertext) {
    if (ciphertext == null || ciphertext.length() == 0) {
        return ciphertext;
    }
    try {
        final byte[] encryptedBytes = Base64.decode(ciphertext, Base64.DEFAULT);
        final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        final SecretKey key = keyFactory.generateSecret(new PBEKeySpec(secret));
        final Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
        pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(getSalt(), 20));
        final byte[] decryptedBytes = pbeCipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, UTF8);
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}