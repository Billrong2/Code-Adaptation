public String encrypt(final Context context, final String value) {
    /**
     * Encrypts a string using a device-scoped salt derived from ANDROID_ID.
     * <p>
     * This method intentionally binds encrypted output to the current device
     * (via {@link android.provider.Settings.Secure#ANDROID_ID}); values encrypted
     * on one device are not expected to be decryptable on another.
     * </p>
     *
     * @param context a non-null {@link android.content.Context} used to resolve ANDROID_ID
     * @param value   the plain text value to encrypt; if null, this method returns null
     * @return the Base64-encoded encrypted value, or null if {@code value} is null
     * @throws RuntimeException if encryption fails or context is null
     */
    if (value == null) {
        return null;
    }
    if (context == null) {
        throw new RuntimeException("Context must not be null");
    }

    try {
        final byte[] bytes = value.getBytes(UTF8);
        final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        final SecretKey key = keyFactory.generateSecret(new PBEKeySpec(mSerkit));
        final Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
        final byte[] salt = Settings.Secure
                .getString(context.getContentResolver(), Settings.Secure.ANDROID_ID)
                .getBytes(UTF8);
        pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(salt, 20));
        return new String(Base64.encode(pbeCipher.doFinal(bytes), Base64.NO_WRAP), UTF8);
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}