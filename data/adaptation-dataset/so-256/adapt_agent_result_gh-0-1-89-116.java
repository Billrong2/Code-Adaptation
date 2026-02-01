@Override
public void add(HttpUrl url, Cookie cookie)
{
    if (url == null || cookie == null)
        return;

    final String host = url.host();
    if (host == null || host.length() == 0)
        return;

    final String token;
    try
    {
        token = getCookieToken(cookie);
    } catch (IllegalArgumentException e)
    {
        Log.d(LOG_TAG, "IllegalArgumentException in getCookieToken", e);
        return;
    }

    // Non-persistent cookies should not be kept or persisted
    if (!cookie.persistent())
    {
        ConcurrentHashMap<String, Cookie> hostCookies = cookies.get(host);
        if (hostCookies == null)
            return;

        hostCookies.remove(token);

        SharedPreferences.Editor prefsWriter = cookiePrefs.edit();
        prefsWriter.remove(COOKIE_NAME_PREFIX + token);
        if (!hostCookies.isEmpty())
        {
            prefsWriter.putString(host, TextUtils.join(",", hostCookies.keySet()));
        } else
        {
            prefsWriter.remove(host);
        }
        prefsWriter.apply();
        return;
    }

    // Persistent cookie: add or update
    ConcurrentHashMap<String, Cookie> hostCookies = cookies.get(host);
    if (hostCookies == null)
    {
        hostCookies = new ConcurrentHashMap<String, Cookie>();
        cookies.put(host, hostCookies);
    }

    hostCookies.put(token, cookie);

    try
    {
        String encodedCookie = encodeCookie(new SerializableHttpCookie(cookie));
        if (encodedCookie == null)
            return;

        SharedPreferences.Editor prefsWriter = cookiePrefs.edit();
        prefsWriter.putString(COOKIE_NAME_PREFIX + token, encodedCookie);
        prefsWriter.putString(host, TextUtils.join(",", hostCookies.keySet()));
        prefsWriter.apply();
    } catch (Exception e)
    {
        Log.d(LOG_TAG, "Exception while persisting cookie", e);
    }
}