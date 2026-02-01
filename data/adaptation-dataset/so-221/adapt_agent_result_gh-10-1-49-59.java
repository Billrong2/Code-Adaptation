private java.util.Map<String, String> splitQuery(java.net.URL url) throws java.io.UnsupportedEncodingException {
    // Source adapted from Stack Overflow: <SO link/ID placeholder>
    java.util.Map<String, String> query_pairs = new java.util.LinkedHashMap<String, String>();
    String query = url.getQuery();
    String[] pairs = query.split("&");
    for (String pair : pairs) {
        int idx = pair.indexOf("=");
        query_pairs.put(java.net.URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
                java.net.URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
    }
    return query_pairs;
}