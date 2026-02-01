/**
     * Encrypts a string using a device-scoped key derived from the Android ID.
     * <p>
     * This encryption is intended for obfuscating locally stored values (e.g., preferences)
     * on the same device. Encrypted values are not portable across devices because the salt
     * is derived from {@link android.provider.Settings.Secure#ANDROID_ID}.
     * </p>
     *
     * @param context a non-null {@link android.content.Context} used to access the Android ID
     * @param value   the plain text value to encrypt; if {@code null}, this method returns {@code null}
     * @return the Base64-encoded encrypted value, or {@code null} if {@code value} is {@code null}
     */
    public String encrypt(Context context, String value) {
        if (value == null) {
            return null;
        }
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null");
        }

        try {
            final String algorithm = "PBEWithMD5AndDES";
            final int iterationCount = 20;

            final String androidId = Settings.Secure.getString(
                    context.getContentResolver(), Settings.Secure.ANDROID_ID);
            if (androidId == null || androidId.length() == 0) {
                throw new IllegalStateException("ANDROID_ID is not available for encryption salt");
            }

            final byte[] bytes = value.getBytes(UTF8);
            final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm);
            final SecretKey secretKey = keyFactory.generateSecret(new PBEKeySpec(mSerkit));
            final Cipher pbeCipher = Cipher.getInstance(algorithm);
            pbeCipher.init(
                    Cipher.ENCRYPT_MODE,
                    secretKey,
                    new PBEParameterSpec(androidId.getBytes(UTF8), iterationCount)
            );

            return new String(
                    Base64.encode(pbeCipher.doFinal(bytes), Base64.NO_WRAP),
                    UTF8
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }