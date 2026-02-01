public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
        if (context == null) {
            return;
        }
        AuthState authState = (AuthState) context.getAttribute(HttpClientContext.TARGET_AUTH_STATE);
        if (authState == null) {
            return;
        }
        // Do nothing if an auth scheme is already set
        if (authState.getAuthScheme() != null) {
            return;
        }
        // Retrieve preconfigured preemptive auth scheme from context
        AuthScheme preemptiveScheme = (AuthScheme) context.getAttribute("preemptive-auth");
        if (preemptiveScheme == null) {
            return;
        }
        CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute(HttpClientContext.CREDS_PROVIDER);
        HttpHost targetHost = (HttpHost) context.getAttribute(HttpCoreContext.HTTP_TARGET_HOST);
        if (credsProvider == null || targetHost == null) {
            return;
        }
        AuthScope authScope = new AuthScope(targetHost.getHostName(), targetHost.getPort());
        Credentials credentials = credsProvider.getCredentials(authScope);
        // Update auth state using the preconfigured scheme and retrieved credentials
        authState.update(preemptiveScheme, credentials);
    }