public static String decrypt(final String key1, final String iv1, final String cipherText) {
    if (key1 == null || iv1 == null || cipherText == null) {
        return null;
    }
    if (key1.length() == 0 || iv1.length() == 0 || cipherText.length() == 0) {
        return null;
    }
    try {
        IvParameterSpec iv = new IvParameterSpec(iv1.getBytes(Constants.BASE_ENCODING));
        SecretKeySpec skeySpec = new SecretKeySpec(key1.getBytes(Constants.BASE_ENCODING), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
        byte[] decodedCipherText = Base64.decodeBase64(cipherText);
        if (decodedCipherText == null || decodedCipherText.length == 0) {
            return null;
        }
        byte[] original = cipher.doFinal(decodedCipherText);
        return new String(original);
    } catch (Exception ex) {
        return null;
    }
}