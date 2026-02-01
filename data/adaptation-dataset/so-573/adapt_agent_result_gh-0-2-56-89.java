public AdditionalKeyStoresTrustManager(final KeyStore... additionalKeyStores) throws KeyStoreException, NoSuchAlgorithmException {
    final ArrayList<TrustManagerFactory> factories = new ArrayList<TrustManagerFactory>();

    final KeyStore[] keyStores = (additionalKeyStores == null)
            ? new KeyStore[0]
            : additionalKeyStores.clone();

    // The default TrustManager with the default KeyStore
    final TrustManagerFactory defaultFactory = TrustManagerFactory.getInstance(
            TrustManagerFactory.getDefaultAlgorithm()
    );
    defaultFactory.init((KeyStore) null);
    factories.add(defaultFactory);

    for (final KeyStore keyStore : keyStores) {
        if (keyStore == null) {
            continue;
        }
        final TrustManagerFactory additionalFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm()
        );
        additionalFactory.init(keyStore);
        factories.add(additionalFactory);
    }

    /*
     * Iterate over the returned trust managers and collect
     * those that are instances of X509TrustManager.
     */
    for (final TrustManagerFactory factory : factories) {
        for (final TrustManager trustManager : factory.getTrustManagers()) {
            if (trustManager instanceof X509TrustManager) {
                x509TrustManagers.add((X509TrustManager) trustManager);
            }
        }
    }

    if (x509TrustManagers.isEmpty()) {
        throw new IllegalStateException("No X509TrustManagers could be initialized");
    }
}