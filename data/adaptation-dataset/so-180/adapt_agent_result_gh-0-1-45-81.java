public static String getCertificateSHA1Fingerprint(Context context) {
    if (context == null) {
        Log.e(TAG, "Context is null");
        return null;
    }

    final String CERT_TYPE_X509 = "X509";
    final String DIGEST_SHA1 = "SHA1";
    final int SIGNATURE_FLAGS = PackageManager.GET_SIGNATURES;

    try {
        PackageManager pm = context.getPackageManager();
        if (pm == null) {
            Log.e(TAG, "PackageManager is null");
            return null;
        }

        String packageName = context.getPackageName();
        PackageInfo packageInfo = pm.getPackageInfo(packageName, SIGNATURE_FLAGS);
        if (packageInfo == null || packageInfo.signatures == null || packageInfo.signatures.length == 0) {
            Log.e(TAG, "PackageInfo or signatures are unavailable");
            return null;
        }

        Signature signature = packageInfo.signatures[0];
        byte[] certBytes = signature.toByteArray();

        CertificateFactory certificateFactory = CertificateFactory.getInstance(CERT_TYPE_X509);
        try (InputStream inputStream = new ByteArrayInputStream(certBytes)) {
            X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(inputStream);
            if (certificate == null) {
                Log.e(TAG, "X509Certificate generation failed");
                return null;
            }

            MessageDigest messageDigest = MessageDigest.getInstance(DIGEST_SHA1);
            byte[] publicKey = messageDigest.digest(certificate.getEncoded());
            return byte2HexFormatted(publicKey);
        }
    } catch (PackageManager.NameNotFoundException e) {
        Log.e(TAG, "Package name not found", e);
    } catch (CertificateException e) {
        Log.e(TAG, "Certificate error while computing SHA1 fingerprint", e);
    } catch (NoSuchAlgorithmException e) {
        Log.e(TAG, "SHA1 algorithm not available", e);
    }

    return null;
}