protected String[] GetCipherList() {
        // Preferred cipher suites in priority order: ChaCha20, then AES-GCM/CBC, then RSA fallbacks
        final String[] preferredCiphers = new String[] {
                "TLS_CHACHA20_POLY1305_SHA256",
                "TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256",
                "TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256",
                "TLS_AES_256_GCM_SHA384",
                "TLS_AES_128_GCM_SHA256",
                "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384",
                "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
                "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256",
                "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
                "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384",
                "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384",
                "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256",
                "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256",
                "TLS_RSA_WITH_AES_256_GCM_SHA384",
                "TLS_RSA_WITH_AES_128_GCM_SHA256",
                "TLS_RSA_WITH_AES_256_CBC_SHA",
                "TLS_RSA_WITH_AES_128_CBC_SHA"
        };

        // Sensible fallback set if anything goes wrong
        final String[] fallbackCiphers = new String[] {
                "TLS_AES_256_GCM_SHA384",
                "TLS_AES_128_GCM_SHA256",
                "TLS_RSA_WITH_AES_256_CBC_SHA",
                "TLS_RSA_WITH_AES_128_CBC_SHA",
                "TLS_EMPTY_RENEGOTIATION_INFO_SCSV"
        };

        String[] availableCiphers;
        try {
                javax.net.ssl.SSLSocketFactory factory = m_ctx.getSocketFactory();
                availableCiphers = factory.getSupportedCipherSuites();
                if (availableCiphers == null || availableCiphers.length == 0) {
                        return java.util.Arrays.copyOf(fallbackCiphers, fallbackCiphers.length);
                }
                java.util.Arrays.sort(availableCiphers);
        } catch (Exception e) {
                return java.util.Arrays.copyOf(fallbackCiphers, fallbackCiphers.length);
        }

        java.util.List<String> selected = new java.util.ArrayList<String>();
        for (int i = 0; i < preferredCiphers.length; i++) {
                if (java.util.Arrays.binarySearch(availableCiphers, preferredCiphers[i]) >= 0) {
                        selected.add(preferredCiphers[i]);
                }
        }

        // Always include secure renegotiation SCSV at the end
        if (!selected.contains("TLS_EMPTY_RENEGOTIATION_INFO_SCSV")) {
                selected.add("TLS_EMPTY_RENEGOTIATION_INFO_SCSV");
        }

        return selected.toArray(new String[selected.size()]);
    }