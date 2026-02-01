@Override
public Map<String, List<String>> get(final URI uri, final Map<String, List<String>> requestHeaders) throws IOException {
    if (uri == null) {
        throw new IOException("URI must not be null");
    }
    final String url = uri.toString();
    String cookie = null;
    try {
        cookie = webkitCookieManager.getCookie(url);
    } catch (RuntimeException e) {
        // propagate as IOException to match CookieHandler contract
        throw new IOException("Failed to get cookies for URI: " + url, e);
    }
    if (cookie == null || cookie.length() == 0) {
        return java.util.Collections.emptyMap();
    }
    final Map<String, List<String>> headers = new HashMap<String, List<String>>(1);
    headers.put("Cookie", Arrays.asList(cookie));
    return headers;
}