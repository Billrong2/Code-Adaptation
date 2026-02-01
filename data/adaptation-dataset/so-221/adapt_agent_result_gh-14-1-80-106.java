public static Map<String, String> getSimpleQueryMap(String uri) {
    Map<String, String> queryMap = new LinkedHashMap<>();
    if (uri == null || uri.length() == 0) {
        return queryMap;
    }
    int questionMarkIndex = uri.indexOf("?");
    if (questionMarkIndex < 0 || questionMarkIndex == uri.length() - 1) {
        return queryMap;
    }
    String query = uri.substring(questionMarkIndex + 1);
    if (query.length() == 0) {
        return queryMap;
    }
    String[] pairs = query.split("&");
    for (String pair : pairs) {
        if (pair == null || pair.length() == 0) {
            continue;
        }
        int idx = pair.indexOf('=');
        if (idx <= 0 || idx == pair.length() - 1) {
            continue;
        }
        try {
            String key = URLDecoder.decode(pair.substring(0, idx), "UTF-8");
            String value = URLDecoder.decode(pair.substring(idx + 1), "UTF-8");
            queryMap.put(key, value);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return queryMap;
        }
    }
    return queryMap;
}