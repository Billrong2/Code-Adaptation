private DefaultHttpClient getNewHttpClient() {
    final int CONNECTION_TIMEOUT_MS = 15000;
    final int SOCKET_TIMEOUT_MS = 45000;
    try {
        org.apache.http.params.HttpParams params = new org.apache.http.params.BasicHttpParams();
        org.apache.http.protocol.HttpProtocolParams.setVersion(params, org.apache.http.HttpVersion.HTTP_1_1);
        org.apache.http.protocol.HttpProtocolParams.setContentCharset(params, org.apache.http.protocol.HTTP.UTF_8);
        org.apache.http.params.HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT_MS);
        org.apache.http.params.HttpConnectionParams.setSoTimeout(params, SOCKET_TIMEOUT_MS);

        // If we are NOT accepting all SSL certificates, return a plain client with normal SSL verification
        if (!this.acceptAllSslCertificates) {
            return new org.apache.http.impl.client.DefaultHttpClient(params);
        }

        // Trust-all SSL setup (conditional)
        java.security.KeyStore trustStore = java.security.KeyStore.getInstance(java.security.KeyStore.getDefaultType());
        trustStore.load(null, null);

        MySSLSocketFactory sslSocketFactory = new MySSLSocketFactory(trustStore);
        sslSocketFactory.setHostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

        org.apache.http.conn.scheme.SchemeRegistry registry = new org.apache.http.conn.scheme.SchemeRegistry();
        registry.register(new org.apache.http.conn.scheme.Scheme(
                "http",
                org.apache.http.conn.scheme.PlainSocketFactory.getSocketFactory(),
                this.port
        ));
        registry.register(new org.apache.http.conn.scheme.Scheme(
                "https",
                sslSocketFactory,
                this.sslPort
        ));

        org.apache.http.conn.ClientConnectionManager ccm =
                new org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager(params, registry);

        return new org.apache.http.impl.client.DefaultHttpClient(ccm, params);
    } catch (Exception e) {
        // Fallback to a default client with normal SSL verification
        org.apache.http.impl.client.DefaultHttpClient fallback = new org.apache.http.impl.client.DefaultHttpClient();
        try {
            org.apache.http.params.HttpParams fallbackParams = fallback.getParams();
            org.apache.http.params.HttpConnectionParams.setConnectionTimeout(fallbackParams, CONNECTION_TIMEOUT_MS);
            org.apache.http.params.HttpConnectionParams.setSoTimeout(fallbackParams, SOCKET_TIMEOUT_MS);
        } catch (Exception ignore) {
            // ignore any secondary failure
        }
        return fallback;
    }
}