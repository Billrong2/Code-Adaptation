public java.security.PublicKey decodePublicKey(String keyLine) throws java.security.GeneralSecurityException {
    if (keyLine == null || keyLine.trim().isEmpty()) {
        throw new IllegalArgumentException("keyLine is null or empty");
    }

    final String BASE64_PREFIX = "AAAA";

    // initialize parsing state
    this.bytes = null;
    this.pos = 0;

    // look for the Base64 encoded part of the line to decode
    for (String part : keyLine.split(" ")) {
        if (part.startsWith(BASE64_PREFIX)) {
            this.bytes = org.apache.commons.codec.binary.Base64.decodeBase64(part);
            break;
        }
    }

    if (this.bytes == null || this.bytes.length == 0) {
        throw new IllegalArgumentException("no Base64 part to decode");
    }

    try {
        String type = decodeType();

        if ("ssh-rsa".equals(type)) {
            if (pos >= bytes.length) {
                throw new IllegalArgumentException("truncated RSA key data");
            }
            java.math.BigInteger exponent = decodeBigInt();
            java.math.BigInteger modulus = decodeBigInt();
            java.security.spec.RSAPublicKeySpec spec = new java.security.spec.RSAPublicKeySpec(modulus, exponent);
            return java.security.KeyFactory.getInstance("RSA").generatePublic(spec);
        } else if ("ssh-dss".equals(type)) {
            if (pos >= bytes.length) {
                throw new IllegalArgumentException("truncated DSA key data");
            }
            java.math.BigInteger p = decodeBigInt();
            java.math.BigInteger q = decodeBigInt();
            java.math.BigInteger g = decodeBigInt();
            java.math.BigInteger y = decodeBigInt();
            java.security.spec.DSAPublicKeySpec spec = new java.security.spec.DSAPublicKeySpec(y, p, q, g);
            return java.security.KeyFactory.getInstance("DSA").generatePublic(spec);
        } else {
            throw new IllegalArgumentException("unknown key type " + type);
        }
    } catch (IndexOutOfBoundsException e) {
        throw new IllegalArgumentException("invalid or truncated key data", e);
    }
}