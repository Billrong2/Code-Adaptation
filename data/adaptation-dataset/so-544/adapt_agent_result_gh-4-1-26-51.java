    /**
     * Generates a hexadecimal hash for the given text using the specified algorithm.
     * <p>
     * Supported algorithms include MD5, SHA1, and SHA-256.
     * If a non-blank salt is provided, it will be appended to the input text
     * prior to hashing.
     * 
     * @param txt the text in plain format
     * @param salt optional salt to append to the text before hashing (may be null or blank)
     * @param algorithm hashing algorithm (MD5, SHA1, or SHA-256)
     * @return hexadecimal hash value, or null if the algorithm is not available
     */
    public static String getHash(final String txt, final String salt, final String algorithm) {
        try {
            if (txt == null) {
                return null;
            }
            String textToHash = txt;
            if (org.apache.commons.lang.StringUtils.isNotBlank(salt)) {
                textToHash = textToHash + salt;
            }
            java.security.MessageDigest md = java.security.MessageDigest.getInstance(algorithm);
            byte[] array = md.digest(textToHash.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            // error action
        }
        return null;
    }