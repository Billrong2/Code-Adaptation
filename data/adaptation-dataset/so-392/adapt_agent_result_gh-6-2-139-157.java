@Override
public boolean remove(HttpUrl uri, Cookie cookie) {
    if (uri == null || cookie == null || cookies == null) {
        return false;
    }
    String host = uri.host();
    String name;
    try {
        name = getCookieToken(cookie);
    } catch (Exception e) {
        Log.d(LOG_TAG, "Failed to derive cookie token", e);
        return false;
    }
    boolean removed = false;
    synchronized (this) {
        ConcurrentHashMap<String, Cookie> hostCookies = cookies.get(host);
        if (hostCookies == null || !hostCookies.containsKey(name)) {
            return false;
        }
        hostCookies.remove(name);
        if (hostCookies.isEmpty()) {
            cookies.remove(host);
        }
        removed = true;
        try {
            SharedPreferences.Editor prefsWriter = cookiePrefs.edit();
            prefsWriter.remove(COOKIE_NAME_PREFIX + name);
            if (cookies.containsKey(host)) {
                prefsWriter.putString(host, TextUtils.join(",", cookies.get(host).keySet()));
            } else {
                prefsWriter.remove(host);
            }
            if (!prefsWriter.commit()) {
                Log.d(LOG_TAG, "Failed to commit cookie removal for " + name);
            }
        } catch (RuntimeException e) {
            Log.d(LOG_TAG, "Exception while removing cookie from persistence", e);
        }
    }
    return removed;
}