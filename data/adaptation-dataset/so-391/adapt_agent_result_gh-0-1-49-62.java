public static Map<String, String> parseQueryString(final String query) throws UnsupportedEncodingException {
    final Map<String, String> params = new HashMap<String, String>();
    if (query == null || query.length() == 0) {
        return params;
    }
    final StringTokenizer pairs = new StringTokenizer(query, "&");
    while (pairs.hasMoreTokens()) {
        final String pair = pairs.nextToken();
        if (pair == null || pair.length() == 0) {
            continue;
        }
        final StringTokenizer parts = new StringTokenizer(pair, "=");
        if (!parts.hasMoreTokens()) {
            continue;
        }
        final String name = URLDecoder.decode(parts.nextToken(), "UTF-8");
        final String value;
        if (parts.hasMoreTokens()) {
            value = URLDecoder.decode(parts.nextToken(), "UTF-8");
        } else {
            value = "";
        }
        params.put(name, value);
    }
    return params;
}