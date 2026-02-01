public static String encrypt(final String secretKey, final String ivString, final String plainText) {
        if (secretKey == null || secretKey.isEmpty() || ivString == null || ivString.isEmpty() || plainText == null) {
            return null;
        }
        try {
            final byte[] keyBytes = secretKey.getBytes(Constants.BASE_ENCODING);
            final byte[] ivBytes = ivString.getBytes(Constants.BASE_ENCODING);
            if (!(keyBytes.length == 16 || keyBytes.length == 24 || keyBytes.length == 32)) {
                return null;
            }
            if (ivBytes.length != 16) {
                return null;
            }
            final IvParameterSpec iv = new IvParameterSpec(ivBytes);
            final SecretKeySpec skeySpec = new SecretKeySpec(keyBytes, "AES");
            final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            final byte[] encrypted = cipher.doFinal(plainText.getBytes(Constants.BASE_ENCODING));
            return Base64.encodeBase64String(encrypted);
        } catch (java.io.UnsupportedEncodingException | java.security.GeneralSecurityException ex) {
            return null;
        }
    }