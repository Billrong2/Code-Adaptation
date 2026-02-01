public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
    if (context == null) {
        return;
    }
    final HttpClientContext clientContext = HttpClientContext.adapt(context);
    final AuthState authState = clientContext.getTargetAuthState();
    if (authState == null) {
        return;
    }

    // Only initialize authentication state when no scheme has been set yet
    if (authState.getAuthScheme() == null) {
        final CredentialsProvider credsProvider = clientContext.getCredentialsProvider();
        final HttpHost targetHost = clientContext.getTargetHost();
        if (credsProvider == null || targetHost == null) {
            return;
        }

        final int port = targetHost.getPort() >= 0 ? targetHost.getPort() : AuthScope.ANY_PORT;
        final Credentials creds = credsProvider.getCredentials(new AuthScope(targetHost.getHostName(), port));
        if (creds == null) {
            throw new HttpException("No credentials available for target host");
        }

        // Always initialize using the configured scheme and resolved credentials
        authState.update(authScheme, creds);
    }
}