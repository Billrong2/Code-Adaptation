protected String[] getCipherList() {
        // Ordered preference: ChaCha20/ECDHE first, then AES-GCM, AES-CBC, RSA as last resort
        final String[] preferredCiphers = new String[] {
                "TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256",
                "TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256",
                "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256",
                "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
                "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384",
                "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
                "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256",
                "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256",
                "TLS_RSA_WITH_AES_128_GCM_SHA256",
                "TLS_RSA_WITH_AES_256_GCM_SHA384"
        };

        final String scsv = "TLS_EMPTY_RENEGOTIATION_INFO_SCSV";

        try {
            final javax.net.ssl.SSLSocketFactory factory = mCtx.getSocketFactory();
            String[] availableCiphers = factory.getSupportedCipherSuites();
            if (availableCiphers == null || availableCiphers.length == 0) {
                throw new IllegalStateException("No supported cipher suites");
            }

            java.util.Arrays.sort(availableCiphers);
            final java.util.List<String> selected = new java.util.ArrayList<String>();

            for (int i = 0; i < preferredCiphers.length; i++) {
                if (java.util.Arrays.binarySearch(availableCiphers, preferredCiphers[i]) >= 0) {
                    selected.add(preferredCiphers[i]);
                }
            }

            // Ensure non-empty result by falling back to a minimal safe set if needed
            if (selected.isEmpty()) {
                for (int i = 0; i < availableCiphers.length; i++) {
                    selected.add(availableCiphers[i]);
                }
            }

            // Always append SCSV defensively
            if (!selected.contains(scsv)) {
                selected.add(scsv);
            }

            return selected.toArray(new String[0]);
        } catch (Exception e) {
            // Sensible fallback cipher list on error
            return new String[] {
                    "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
                    "TLS_RSA_WITH_AES_128_GCM_SHA256",
                    scsv
            };
        }
    }