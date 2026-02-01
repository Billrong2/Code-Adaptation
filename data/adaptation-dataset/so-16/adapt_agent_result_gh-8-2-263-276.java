public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
        if (context == null) {
            return;
        }
        final org.apache.http.client.protocol.HttpClientContext clientContext = org.apache.http.client.protocol.HttpClientContext.adapt(context);
        final AuthState authState = clientContext.getTargetAuthState();
        if (authState == null) {
            return;
        }
        // Guard clause: authentication already initialized
        if (authState.getAuthScheme() != null) {
            return;
        }
        final Object preemptive = context.getAttribute("preemptive-auth");
        if (!(preemptive instanceof AuthScheme)) {
            return;
        }
        final AuthScheme authScheme = (AuthScheme) preemptive;
        final CredentialsProvider credentialsProvider = clientContext.getCredentialsProvider();
        final HttpHost targetHost = (HttpHost) context.getAttribute(org.apache.http.protocol.HttpCoreContext.HTTP_TARGET_HOST);
        Credentials credentials = null;
        if (credentialsProvider != null && targetHost != null) {
            credentials = credentialsProvider.getCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()));
        }
        // Update auth state in one call; credentials may be null and handled downstream
        authState.update(authScheme, credentials);
    }