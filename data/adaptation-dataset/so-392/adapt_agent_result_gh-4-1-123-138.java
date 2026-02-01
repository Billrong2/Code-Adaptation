@Override
public boolean remove(URI uri, HttpCookie cookie) {
    if (uri == null || cookie == null) {
        return false;
    }
    String host = uri.getHost();
    if (host == null) {
        return false;
    }
    String token = getCookieToken(uri, cookie);
    if (token == null) {
        return false;
    }
    boolean removed = false;
    synchronized (mCookieMap) {
        try {
            ConcurrentHashMap<String, HttpCookie> hostCookies = mCookieMap.get(host);
            if (hostCookies == null || !hostCookies.containsKey(token)) {
                return false;
            }
            hostCookies.remove(token);
            removed = true;
            if (hostCookies.isEmpty()) {
                mCookieMap.remove(host);
            }
            SharedPreferences.Editor editor = mCookiePrefs.edit();
            if (editor == null) {
                return removed;
            }
            // remove encoded cookie entry
            editor.remove(COOKIE_NAME_PREFIX + token);
            // rewrite host index entry
            if (mCookieMap.containsKey(host) && !mCookieMap.get(host).isEmpty()) {
                editor.putString(host, TextUtils.join(",", mCookieMap.get(host).keySet()));
            } else {
                editor.remove(host);
            }
            editor.commit();
        } catch (RuntimeException e) {
            Log.d(LOG_TAG, "Exception in remove", e);
            return false;
        }
    }
    return removed;
}