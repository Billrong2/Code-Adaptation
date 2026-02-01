public void process(final org.apache.http.HttpRequest request, final org.apache.http.protocol.HttpContext context) throws org.apache.http.HttpException, java.io.IOException {
        if (context == null) {
            return;
        }

        final org.apache.http.auth.AuthState authState = (org.apache.http.auth.AuthState) context.getAttribute(org.apache.http.client.protocol.HttpClientContext.TARGET_AUTH_STATE);
        if (authState == null) {
            return;
        }

        // Do nothing if an auth scheme is already set
        if (authState.getAuthScheme() != null) {
            return;
        }

        // Preemptive auth is opt-in: require an AuthScheme in the context
        final Object schemeAttr = context.getAttribute("preemptive-auth");
        if (!(schemeAttr instanceof org.apache.http.auth.AuthScheme)) {
            return;
        }
        final org.apache.http.auth.AuthScheme authScheme = (org.apache.http.auth.AuthScheme) schemeAttr;

        final org.apache.http.client.CredentialsProvider credsProvider =
                (org.apache.http.client.CredentialsProvider) context.getAttribute(org.apache.http.client.protocol.HttpClientContext.CREDS_PROVIDER);
        if (credsProvider == null) {
            return;
        }

        final org.apache.http.HttpHost targetHost =
                (org.apache.http.HttpHost) context.getAttribute(org.apache.http.protocol.HttpCoreContext.HTTP_TARGET_HOST);
        if (targetHost == null) {
            return;
        }

        final org.apache.http.auth.Credentials credentials = credsProvider.getCredentials(
                new org.apache.http.auth.AuthScope(targetHost.getHostName(), targetHost.getPort()));
        if (credentials == null) {
            return;
        }

        // Initialize preemptive authentication using the provided scheme and credentials
        authState.update(authScheme, credentials);
    }