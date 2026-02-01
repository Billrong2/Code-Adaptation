public static void main(final String[] args) {
    try {
        // Generate RSA key pair at runtime
        final java.security.KeyPairGenerator keyGen = java.security.KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        final java.security.KeyPair keyPair = keyGen.generateKeyPair();
        final java.security.PublicKey publicKey = keyPair.getPublic();
        final java.security.PrivateKey privateKey = keyPair.getPrivate();

        // Default message if no arguments are supplied
        final String[] messageParts = (args == null || args.length == 0)
                ? new String[]{"Hello", "from", "SignatureOutputStream", "demo"}
                : args;

        // Sign data while capturing the exact bytes that are signed
        final byte[] signature;
        final byte[] dataBytes;
        try (final java.io.ByteArrayOutputStream dataBuffer = new java.io.ByteArrayOutputStream()) {
            signature = signData(dataBuffer, privateKey, messageParts);
            dataBytes = dataBuffer.toByteArray();
        }

        if (publicKey == null || signature == null || dataBytes == null) {
            throw new IllegalStateException("Key, signature, or data must not be null");
        }

        // Successful verification
        System.err.println("== Verification with original data/signature ==");
        verify(publicKey, signature, dataBytes);

        // Mutate data bytes to demonstrate failure
        System.err.println("== Verification after mutating data bytes ==");
        final byte[] mutatedData = dataBytes.clone();
        if (mutatedData.length > 0) {
            mutatedData[0] ^= 0x01;
        }
        verify(publicKey, signature, mutatedData);

        // Restore data and verify again
        System.err.println("== Verification after restoring data bytes ==");
        verify(publicKey, signature, dataBytes);

        // Mutate signature bytes to demonstrate failure
        System.err.println("== Verification after mutating signature bytes ==");
        final byte[] mutatedSignature = signature.clone();
        if (mutatedSignature.length > 0) {
            mutatedSignature[0] ^= 0x01;
        }
        verify(publicKey, mutatedSignature, dataBytes);

    } catch (Exception ex) {
        // Demo-friendly error reporting
        System.err.println("Demo failed: " + ex.getMessage());
        ex.printStackTrace(System.err);
    }
}