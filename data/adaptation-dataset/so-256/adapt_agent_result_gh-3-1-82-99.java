@Override
public void add(final URI uri, final HttpCookie cookie) {
    if (uri == null || cookie == null) {
        return;
    }
    final String host = uri.getHost();
    if (host == null || host.length() == 0) {
        return;
    }

    // Remove expired cookies immediately
    if (cookie.hasExpired()) {
        remove(uri, cookie);
        return;
    }

    final String token = getCookieToken(uri, cookie);
    if (token == null || token.length() == 0) {
        return;
    }

    // In-memory update
    ConcurrentHashMap<String, HttpCookie> hostCookies = mCookieMap.get(host);
    if (hostCookies == null) {
        hostCookies = new ConcurrentHashMap<String, HttpCookie>();
        mCookieMap.put(host, hostCookies);
    }
    hostCookies.put(token, cookie);

    // Persist immediately
    final SharedPreferences.Editor prefsWriter = mCookiePrefs.edit();
    try {
        final String encodedCookie = encodeCookie(new SerializableHttpCookie(cookie));
        if (encodedCookie != null) {
            prefsWriter.putString(COOKIE_NAME_PREFIX + token, encodedCookie);
        }
        prefsWriter.putString(host, TextUtils.join(",", hostCookies.keySet()));
        prefsWriter.commit();
    } catch (RuntimeException e) {
        Log.d(LOG_TAG, "Error while persisting cookie", e);
    }
}