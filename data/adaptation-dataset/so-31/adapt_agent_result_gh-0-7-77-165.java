@Override
    public java.net.URI getLocationURI(
            final cz.msebera.android.httpclient.HttpResponse response,
            final cz.msebera.android.httpclient.protocol.HttpContext context) throws cz.msebera.android.httpclient.ProtocolException {
        if (response == null) {
            throw new IllegalArgumentException("HTTP response may not be null");
        }

        // Get the location header to find out where to redirect to
        final cz.msebera.android.httpclient.Header locationHeader = response.getFirstHeader("location");
        if (locationHeader == null) {
            // Got a redirect response, but no location header
            throw new cz.msebera.android.httpclient.ProtocolException(
                    "Received redirect response " + response.getStatusLine()
                            + " but no location header");
        }

        final String rawLocation = locationHeader.getValue();
        if (rawLocation == null) {
            throw new cz.msebera.android.httpclient.ProtocolException("Invalid redirect URI: null");
        }

        // Normalize the Location header by trimming and replacing spaces before URI parsing
        final String location = rawLocation.trim().replaceAll(" ", "%20");

        java.net.URI uri;
        try {
            uri = new java.net.URI(location);
        } catch (java.net.URISyntaxException ex) {
            throw new cz.msebera.android.httpclient.ProtocolException("Invalid redirect URI: " + location, ex);
        }

        final cz.msebera.android.httpclient.params.HttpParams params = response.getParams();
        // RFC2616 demands the location value be a complete URI
        if (!uri.isAbsolute()) {
            if (params.isParameterTrue(cz.msebera.android.httpclient.client.params.ClientPNames.REJECT_RELATIVE_REDIRECT)) {
                throw new cz.msebera.android.httpclient.ProtocolException("Relative redirect location '" + uri + "' not allowed");
            }

            // Adjust location URI
            final cz.msebera.android.httpclient.HttpHost target =
                    (cz.msebera.android.httpclient.HttpHost) context.getAttribute(
                            cz.msebera.android.httpclient.protocol.ExecutionContext.HTTP_TARGET_HOST);
            if (target == null) {
                throw new IllegalStateException("Target host not available in the HTTP context");
            }

            final cz.msebera.android.httpclient.HttpRequest request =
                    (cz.msebera.android.httpclient.HttpRequest) context.getAttribute(
                            cz.msebera.android.httpclient.protocol.ExecutionContext.HTTP_REQUEST);
            try {
                final java.net.URI requestURI = new java.net.URI(request.getRequestLine().getUri());
                final java.net.URI absoluteRequestURI =
                        cz.msebera.android.httpclient.client.utils.URIUtils.rewriteURI(requestURI, target, true);
                uri = cz.msebera.android.httpclient.client.utils.URIUtils.resolve(absoluteRequestURI, uri);
            } catch (java.net.URISyntaxException ex) {
                throw new cz.msebera.android.httpclient.ProtocolException(ex.getMessage(), ex);
            }
        }

        if (params.isParameterFalse(cz.msebera.android.httpclient.client.params.ClientPNames.ALLOW_CIRCULAR_REDIRECTS)) {
            cz.msebera.android.httpclient.impl.client.RedirectLocations redirectLocations =
                    (cz.msebera.android.httpclient.impl.client.RedirectLocations) context.getAttribute(REDIRECT_LOCATIONS);

            if (redirectLocations == null) {
                redirectLocations = new cz.msebera.android.httpclient.impl.client.RedirectLocations();
                context.setAttribute(REDIRECT_LOCATIONS, redirectLocations);
            }

            final java.net.URI redirectURI;
            if (uri.getFragment() != null) {
                try {
                    final cz.msebera.android.httpclient.HttpHost target =
                            new cz.msebera.android.httpclient.HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
                    redirectURI = cz.msebera.android.httpclient.client.utils.URIUtils.rewriteURI(uri, target, true);
                } catch (java.net.URISyntaxException ex) {
                    throw new cz.msebera.android.httpclient.ProtocolException(ex.getMessage(), ex);
                }
            } else {
                redirectURI = uri;
            }

            if (redirectLocations.contains(redirectURI)) {
                throw new cz.msebera.android.httpclient.client.CircularRedirectException(
                        "Circular redirect to '" + redirectURI + "'");
            } else {
                redirectLocations.add(redirectURI);
            }
        }

        return uri;
    }