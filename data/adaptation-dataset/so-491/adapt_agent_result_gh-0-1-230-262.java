public static com.android.volley.Cache.Entry parseIgnoreCacheHeaders(com.android.volley.NetworkResponse response, final long refreshMillis, final long expireMillis) {
    if (response == null || response.headers == null) {
        return null;
    }

    final long now = System.currentTimeMillis();
    final java.util.Map<String, String> headers = response.headers;

    long serverDate = 0L;
    String headerValue = headers.get("Date");
    if (headerValue != null) {
        serverDate = com.android.volley.toolbox.HttpHeaderParser.parseDateAsEpoch(headerValue);
    }

    final long softExpire = now + refreshMillis;
    final long ttl = now + expireMillis;

    com.android.volley.Cache.Entry entry = new com.android.volley.Cache.Entry();
    entry.data = response.data;
    entry.etag = null; // Explicitly ignore ETag
    entry.softTtl = softExpire;
    entry.ttl = ttl;
    entry.serverDate = serverDate;
    entry.responseHeaders = headers;

    return entry;
}