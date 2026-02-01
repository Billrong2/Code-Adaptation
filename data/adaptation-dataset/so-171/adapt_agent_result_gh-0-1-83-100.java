private static void verify(final PublicKey key, final byte[] signature, final byte[] data)
        throws IOException, GeneralSecurityException
{
    if (key == null) {
        throw new IllegalArgumentException("key must not be null");
    }
    if (signature == null || signature.length == 0) {
        throw new IllegalArgumentException("signature must not be null or empty");
    }
    if (data == null) {
        throw new IllegalArgumentException("data must not be null");
    }

    final Signature sig = Signature.getInstance("SHA1withRSA");
    sig.initVerify(key);

    try (ByteArrayOutputStream collector = new ByteArrayOutputStream(data.length);
         OutputStream checker = new SignatureOutputStream(collector, sig))
    {
        checker.write(data);
    }

    if (sig.verify(signature)) {
        System.err.println("Signature okay");
    }
    else {
        System.err.println("Signature falsed!");
    }
}