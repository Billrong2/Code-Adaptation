protected String encrypt(String value) {
        try {
            final byte[] plainBytes = value != null ? value.getBytes(UTF8) : new byte[0];
            final byte[] salt = getSalt();
            if (salt == null) {
                throw new IllegalStateException("Salt must not be null");
            }
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            SecretKey key = keyFactory.generateSecret(new PBEKeySpec(secret));
            Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
            pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(salt, 20));
            final byte[] encrypted = pbeCipher.doFinal(plainBytes);
            return new String(Base64.encode(encrypted, Base64.NO_WRAP), UTF8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }