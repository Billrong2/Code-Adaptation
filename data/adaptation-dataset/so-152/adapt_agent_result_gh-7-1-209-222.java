protected String decrypt(String ciphertext) {

        try {
            if (ciphertext == null || ciphertext.length() == 0) {
                return "";
            }
            final byte[] encryptedBytes = android.util.Base64.decode(ciphertext, android.util.Base64.DEFAULT);
            final javax.crypto.SecretKeyFactory keyFactory = javax.crypto.SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            final javax.crypto.SecretKey key = keyFactory.generateSecret(new javax.crypto.spec.PBEKeySpec(getSpecialCode()));
            final javax.crypto.Cipher pbeCipher = javax.crypto.Cipher.getInstance("PBEWithMD5AndDES");
            pbeCipher.init(javax.crypto.Cipher.DECRYPT_MODE, key,
                    new javax.crypto.spec.PBEParameterSpec(getAndroidId().getBytes(UTF8), 20));
            final byte[] decryptedBytes = pbeCipher.doFinal(encryptedBytes);
            return new String(decryptedBytes, UTF8);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }