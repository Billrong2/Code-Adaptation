public static DefaultHttpClient getDefaultHttpClient() {
	// For newer Android versions, use the standard secure HttpClient
	if (android.os.Build.VERSION.SDK_INT > 10) {
		return new org.apache.http.impl.client.DefaultHttpClient();
	}
	// Legacy devices: fall back to trust-all SSL behavior
	try {
		final java.security.KeyStore trustStore = java.security.KeyStore.getInstance(java.security.KeyStore.getDefaultType());
		trustStore.load(null, null);

		final MySSLSocketFactory sslSocketFactory = new MySSLSocketFactory(trustStore);
		sslSocketFactory.setHostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

		final org.apache.http.params.HttpParams params = new org.apache.http.params.BasicHttpParams();
		org.apache.http.params.HttpProtocolParams.setVersion(params, org.apache.http.HttpVersion.HTTP_1_1);
		org.apache.http.params.HttpProtocolParams.setContentCharset(params, org.apache.http.protocol.HTTP.UTF_8);

		final org.apache.http.conn.scheme.SchemeRegistry registry = new org.apache.http.conn.scheme.SchemeRegistry();
		registry.register(new org.apache.http.conn.scheme.Scheme("http", org.apache.http.conn.scheme.PlainSocketFactory.getSocketFactory(), 80));
		registry.register(new org.apache.http.conn.scheme.Scheme("https", sslSocketFactory, 443));

		final org.apache.http.conn.ClientConnectionManager connectionManager =
				new org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager(params, registry);

		return new org.apache.http.impl.client.DefaultHttpClient(connectionManager, params);
	} catch (Exception e) {
		// Fallback to a plain DefaultHttpClient on any failure
		return new org.apache.http.impl.client.DefaultHttpClient();
	}
}