/**
     * Calculates the MD5 hash for the given input string.
     *
     * @param input the string to hash; may be {@code null}
     * @return the MD5 hash as a hexadecimal string, or an empty string if the input is {@code null}
     */
    public static String md5(final String input) {
        if (input == null) {
            return "";
        }
        try {
            final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(input.getBytes(java.nio.charset.Charset.forName("UTF-8")));
            final byte[] digestBytes = messageDigest.digest();

            final StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < digestBytes.length; i++) {
                hexString.append(Integer.toHexString(0xFF & digestBytes[i]));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not available", e);
        }
    }