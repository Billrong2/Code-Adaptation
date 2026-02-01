@Override
public boolean remove(URI uri, HttpCookie cookie) {
    if (uri == null || cookie == null || uri.getHost() == null) {
        return false;
    }

    final String host = uri.getHost();
    final String token;
    try {
        token = getCookieToken(uri, cookie);
    } catch (RuntimeException e) {
        Log.d(LOG_TAG, "Failed to derive cookie token", e);
        return false;
    }

    boolean removed = false;
    synchronized (this) {
        ConcurrentHashMap<String, HttpCookie> hostCookies = cookies.get(host);
        if (hostCookies == null || !hostCookies.containsKey(token)) {
            return false;
        }

        hostCookies.remove(token);
        removed = true;

        SharedPreferences.Editor prefsWriter = cookiePrefs.edit();
        try {
            // Remove encoded cookie entry
            prefsWriter.remove(COOKIE_NAME_PREFIX + token);

            // Update host's stored cookie-name list
            if (hostCookies.isEmpty()) {
                cookies.remove(host);
                prefsWriter.remove(host);
            } else {
                prefsWriter.putString(host, TextUtils.join(",", hostCookies.keySet()));
            }

            prefsWriter.commit();
        } catch (RuntimeException e) {
            Log.d(LOG_TAG, "Exception while removing cookie from persistence", e);
            return false;
        }
    }

    return removed;
}