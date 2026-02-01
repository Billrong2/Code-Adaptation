@Override
public void add(URI uri, HttpCookie cookie) {
    if (uri == null || cookie == null) {
        return;
    }
    String host = uri.getHost();
    if (host == null) {
        return;
    }

    synchronized (this) {
        String token = getCookieToken(uri, cookie);
        ConcurrentHashMap<String, HttpCookie> hostCookies = cookies.get(host);
        if (hostCookies == null) {
            hostCookies = new ConcurrentHashMap<String, HttpCookie>();
            cookies.put(host, hostCookies);
        }

        SharedPreferences.Editor prefsWriter = cookiePrefs.edit();
        try {
            if (cookie.hasExpired()) {
                // remove expired cookie
                hostCookies.remove(token);
                prefsWriter.remove(COOKIE_NAME_PREFIX + token);
            } else {
                // add or update cookie
                hostCookies.put(token, cookie);
                String encodedCookie = encodeCookie(new SerializableHttpCookie(cookie));
                if (encodedCookie != null) {
                    prefsWriter.putString(COOKIE_NAME_PREFIX + token, encodedCookie);
                }
            }

            // update host token list
            if (hostCookies.isEmpty()) {
                prefsWriter.remove(host);
            } else {
                prefsWriter.putString(host, TextUtils.join(",", hostCookies.keySet()));
            }

            prefsWriter.commit();
            Log.d(LOG_TAG, "Cookie add/update for host=" + host + ", token=" + token);
        } catch (RuntimeException e) {
            Log.d(LOG_TAG, "Error while adding cookie", e);
        }
    }
}