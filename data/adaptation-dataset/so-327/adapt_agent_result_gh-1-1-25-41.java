@Override
public void put(final URI uri, final Map<String, List<String>> responseHeaders) throws IOException {
    // make sure our args are valid
    if (uri == null || responseHeaders == null) {
        return;
    }

    // save our url once
    final String url = uri.toString();

    // go over the headers
    for (final Map.Entry<String, List<String>> entry : responseHeaders.entrySet()) {
        final String headerKey = entry.getKey();

        // ignore headers which aren't cookie related
        if (headerKey == null
                || !(headerKey.equalsIgnoreCase("Set-Cookie")
                || headerKey.equalsIgnoreCase("Set-Cookie2"))) {
            continue;
        }

        final List<String> headerValues = entry.getValue();
        if (headerValues == null) {
            continue;
        }

        // process each of the headers
        for (final String headerValue : headerValues) {
            webkitCookieManager.setCookie(url, headerValue);
        }
    }
}