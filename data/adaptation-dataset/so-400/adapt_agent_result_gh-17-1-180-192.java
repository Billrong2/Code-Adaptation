protected String decrypt(String ciphertext) {
        if (ciphertext == null || ciphertext.length() == 0) {
            return "";
        }
        try {
            final byte[] encryptedBytes = android.util.Base64.decode(ciphertext, android.util.Base64.DEFAULT);
            if (encryptedBytes == null || encryptedBytes.length == 0) {
                return "";
            }
            final int iterationCount = 20;
            final javax.crypto.SecretKeyFactory keyFactory = javax.crypto.SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            final javax.crypto.SecretKey key = keyFactory.generateSecret(new javax.crypto.spec.PBEKeySpec(secret));
            final javax.crypto.Cipher pbeCipher = javax.crypto.Cipher.getInstance("PBEWithMD5AndDES");
            pbeCipher.init(javax.crypto.Cipher.DECRYPT_MODE, key, new javax.crypto.spec.PBEParameterSpec(getSalt(), iterationCount));
            final byte[] plainBytes = pbeCipher.doFinal(encryptedBytes);
            return new String(plainBytes, UTF8);
        } catch (java.lang.IllegalArgumentException e) {
            throw new RuntimeException("Invalid Base64 ciphertext for decryption", e);
        } catch (javax.crypto.BadPaddingException | javax.crypto.IllegalBlockSizeException e) {
            throw new RuntimeException("Decryption failed: invalid ciphertext or key", e);
        } catch (java.security.GeneralSecurityException | java.io.UnsupportedEncodingException e) {
            throw new RuntimeException("Decryption error", e);
        }
    }