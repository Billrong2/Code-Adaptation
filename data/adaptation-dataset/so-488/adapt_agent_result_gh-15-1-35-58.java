public static synchronized HttpClient getHttpClient() {
    // Adapted from a permissive SSL HttpClient example on Stack Overflow (non-functional reference).
    if (client != null) {
        return client;
    }
    try {
        final java.security.KeyStore trustStore = java.security.KeyStore.getInstance(java.security.KeyStore.getDefaultType());
        trustStore.load(null, null);

        // WARNING: This SSL socket factory is intentionally permissive and should not be used in production environments.
        final SSLSocketFactory sslSocketFactory = new LooseSSLSocketFactory(trustStore);
        sslSocketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

        final HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

        final SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        registry.register(new Scheme("https", sslSocketFactory, 443));

        final ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
        client = new DefaultHttpClient(ccm, params);
        return client;
    } catch (java.security.KeyStoreException e) {
        client = new DefaultHttpClient();
        return client;
    } catch (java.security.NoSuchAlgorithmException e) {
        client = new DefaultHttpClient();
        return client;
    } catch (java.security.KeyManagementException e) {
        client = new DefaultHttpClient();
        return client;
    } catch (Exception e) {
        client = new DefaultHttpClient();
        return client;
    }
  }