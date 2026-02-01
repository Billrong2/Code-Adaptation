/**
     * Computes the MD5 hash of the given input string and returns it as a hexadecimal string.
     *
     * @param s the input string to hash
     * @return the MD5 hash represented as a lowercase hexadecimal string
     * @throws RuntimeException if the MD5 algorithm is not available on this platform
     */
    public static String getHashString(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte[] messageDigest = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2) {
                    h = "0" + h;
                }
                hexString.append(h);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "MD5 algorithm not available", e);
            throw new RuntimeException("MD5 algorithm not available", e);
        }
    }