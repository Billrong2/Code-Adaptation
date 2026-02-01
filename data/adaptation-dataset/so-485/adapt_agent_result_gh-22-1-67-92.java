public static javax.net.ssl.SSLSocketFactory getTrustAllSSLSocketFactory() {
    final javax.net.ssl.TrustManager[] trustAllManagers = new javax.net.ssl.TrustManager[] {
        new javax.net.ssl.X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                // trust all clients
            }
            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                // trust all servers
            }
        }
    };

    try {
        final javax.net.ssl.SSLContext sslContext = javax.net.ssl.SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllManagers, new java.security.SecureRandom());
        final javax.net.ssl.SSLSocketFactory socketFactory = sslContext.getSocketFactory();
        return socketFactory != null ? socketFactory : null;
    } catch (java.security.NoSuchAlgorithmException e) {
        return null;
    } catch (java.security.KeyManagementException e) {
        return null;
    }
}