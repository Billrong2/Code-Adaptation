public static String decrypt(byte[] headerSaltAndCipherText, String password) {
        final int HEADER_LENGTH = 8; // "Salted__"
        if (headerSaltAndCipherText == null)
            throw new IllegalStateException("Encrypted payload is null");
        if (password == null)
            throw new IllegalStateException("Password is null");
        if (headerSaltAndCipherText.length < CIPHERTEXT_OFFSET + 1)
            throw new IllegalStateException("Encrypted payload too short");
        try {
            // validate header
            for (int i = 0; i < HEADER_LENGTH; i++) {
                if (headerSaltAndCipherText[i] != "Salted__".getBytes("ASCII")[i])
                    throw new IllegalStateException("Missing OpenSSL Salted__ header");
            }

            final byte[] salt = copyOfRange(headerSaltAndCipherText, SALT_OFFSET, SALT_OFFSET + SALT_SIZE);
            final byte[] cipherText = copyOfRange(headerSaltAndCipherText, CIPHERTEXT_OFFSET,
                    headerSaltAndCipherText.length);
            if (cipherText.length == 0)
                throw new IllegalStateException("No ciphertext present");

            final byte[] passwordBytes = password.getBytes("ASCII");
            final MessageDigest md = MessageDigest.getInstance("MD5");
            final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            final int keySizeBytes = KEY_SIZE_BITS / 8;
            final int ivSizeBytes = cipher.getBlockSize();
            final byte[][] keyAndIv = EVP_BytesToKey(keySizeBytes, ivSizeBytes, md, salt, passwordBytes,
                    ITERATIONS);
            if (keyAndIv == null || keyAndIv.length < 2)
                throw new IllegalStateException("Key derivation failed");

            final byte[] key = keyAndIv[INDEX_KEY];
            final byte[] iv = keyAndIv[INDEX_IV];
            if (key == null || iv == null)
                throw new IllegalStateException("Derived key or IV is null");

            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
            final byte[] plainBytes = cipher.doFinal(cipherText);
            final String plaintext = new String(plainBytes, "ASCII");

            // zero sensitive data
            for (int i = 0; i < passwordBytes.length; i++) passwordBytes[i] = 0;
            for (int i = 0; i < key.length; i++) key[i] = 0;
            for (int i = 0; i < iv.length; i++) iv[i] = 0;
            for (int i = 0; i < plainBytes.length; i++) plainBytes[i] = 0;

            return plaintext;
        } catch (BadPaddingException e) {
            throw new IllegalStateException("Bad padding: likely wrong password or corrupted data", e);
        } catch (IllegalBlockSizeException e) {
            throw new IllegalStateException("Illegal block size: corrupted ciphertext", e);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("ASCII encoding not supported", e);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Cryptographic failure during OpenSSL-compatible decryption", e);
        }
    }