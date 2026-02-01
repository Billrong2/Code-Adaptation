private DefaultHttpClient getNewHttpClient() {
    // If not explicitly accepting all SSL certificates, use the default client
    if (!this.acceptAllSslCertificates) {
        return new DefaultHttpClient();
    }

    final int CONNECT_TIMEOUT_MS = 15 * 1000;
    final int SOCKET_TIMEOUT_MS = 45 * 1000;

    try {
        java.security.KeyStore trustStore = java.security.KeyStore.getInstance(java.security.KeyStore.getDefaultType());
        trustStore.load(null, null);

        // Type as SSLSocketFactory while instantiating the custom implementation
        org.apache.http.conn.ssl.SSLSocketFactory sslSocketFactory = new MySSLSocketFactory(trustStore);
        sslSocketFactory.setHostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

        org.apache.http.params.HttpParams params = new org.apache.http.params.BasicHttpParams();
        org.apache.http.params.HttpProtocolParams.setVersion(params, org.apache.http.HttpVersion.HTTP_1_1);
        org.apache.http.params.HttpProtocolParams.setContentCharset(params, org.apache.http.protocol.HTTP.UTF_8);

        // Configure timeouts
        org.apache.http.params.HttpConnectionParams.setConnectionTimeout(params, CONNECT_TIMEOUT_MS);
        org.apache.http.params.HttpConnectionParams.setSoTimeout(params, SOCKET_TIMEOUT_MS);

        org.apache.http.conn.scheme.SchemeRegistry registry = new org.apache.http.conn.scheme.SchemeRegistry();
        registry.register(new org.apache.http.conn.scheme.Scheme("http", org.apache.http.conn.scheme.PlainSocketFactory.getSocketFactory(), this.port));
        registry.register(new org.apache.http.conn.scheme.Scheme("https", sslSocketFactory, this.sslPort));

        org.apache.http.conn.ClientConnectionManager ccm = new org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager(params, registry);
        return new org.apache.http.impl.client.DefaultHttpClient(ccm, params);
    } catch (Exception e) {
        // Fallback to a default client on any failure
        return new org.apache.http.impl.client.DefaultHttpClient();
    }
}