protected String encrypt(String value) {
        try {
            // Treat null as empty to mirror decrypt behavior and avoid NPEs
            final byte[] clearBytes = value != null ? value.getBytes(UTF8) : new byte[0];
            // Basic validation of required crypto material
            if (SEKRIT == null || SALT == null) {
                throw new IllegalStateException("Encryption key or salt not initialized");
            }
            final int ITERATION_COUNT = 20;
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            SecretKey key = keyFactory.generateSecret(new PBEKeySpec(SEKRIT));
            Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
            pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, ITERATION_COUNT));
            byte[] encrypted = pbeCipher.doFinal(clearBytes);
            // Encode without line wrapping using project-specific Base64Support
            return new String(Base64Support.encode(encrypted, Base64Support.NO_WRAP), UTF8);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }