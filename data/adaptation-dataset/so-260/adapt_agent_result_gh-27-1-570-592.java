public static synchronized void setSSLCertValidation(boolean enabled) {
	final String ORIGINAL_FACTORY_KEY = "itdelatrisu.opsu.Utils.originalSSLSocketFactory";
	try {
		if (enabled) {
			// Restore default SSL certificate validation if previously overridden
			Object original = System.getProperties().get(ORIGINAL_FACTORY_KEY);
			if (original instanceof javax.net.ssl.SSLSocketFactory) {
				javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory((javax.net.ssl.SSLSocketFactory) original);
				System.getProperties().remove(ORIGINAL_FACTORY_KEY);
			}
			return;
		}

		// Disable certificate chain validation (but keep default hostname verification)
		if (!System.getProperties().containsKey(ORIGINAL_FACTORY_KEY)) {
			System.getProperties().put(ORIGINAL_FACTORY_KEY, javax.net.ssl.HttpsURLConnection.getDefaultSSLSocketFactory());
		}

		javax.net.ssl.TrustManager trustAllManager = new javax.net.ssl.X509TrustManager() {
			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[0];
			}

			@Override
			public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
				// trust all clients
			}

			@Override
			public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
				// trust all servers
			}
		};

		javax.net.ssl.SSLContext context = javax.net.ssl.SSLContext.getInstance("TLS");
		context.init(null, new javax.net.ssl.TrustManager[] { trustAllManager }, null);
		javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
	} catch (java.security.GeneralSecurityException e) {
		org.newdawn.slick.util.Log.error("Failed to toggle SSL certificate validation.", e);
		throw new RuntimeException("Could not change SSL certificate validation state.", e);
	}
}