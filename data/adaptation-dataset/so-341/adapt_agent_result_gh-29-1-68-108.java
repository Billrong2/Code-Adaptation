/**
     * Disables HTTPS certificate and hostname validation for test purposes only.
     * <p>
     * This method installs an all-trusting {@link X509TrustManager} and an all-accepting
     * {@link HostnameVerifier} on {@link HttpsURLConnection}. It must never be used in
     * production code as it completely bypasses TLS security checks.
     * </p>
     *
     * @throws GeneralSecurityException if the TLS context cannot be initialized
     */
    public void disableCertificateValidation() throws GeneralSecurityException {
        // Create a trust manager that does not validate certificate chains
        final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            @Override
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
                // trust all clients
            }

            @Override
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
                // trust all servers
            }
        } };

        // Install the all-trusting trust manager using TLS
        final SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

        // Create and install an all-trusting host name verifier
        final HostnameVerifier allHostsValid = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }