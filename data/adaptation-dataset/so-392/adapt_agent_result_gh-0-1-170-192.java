@Override
public boolean remove(HttpUrl uri, Cookie cookie)
{
    if (uri == null || cookie == null)
        return false;

    String host = uri.host();
    if (host == null)
        return false;

    String token = getCookieToken(cookie);
    if (token == null)
        return false;

    ConcurrentHashMap<String, Cookie> hostCookies = cookies.get(host);
    if (hostCookies == null || !hostCookies.containsKey(token))
        return false;

    // Remove from in-memory store
    hostCookies.remove(token);
    if (hostCookies.isEmpty())
        cookies.remove(host);

    // Synchronize persistent store
    SharedPreferences.Editor editor = cookiePrefs.edit();
    try
    {
        // Remove encoded cookie
        editor.remove(COOKIE_NAME_PREFIX + token);

        if (cookies.containsKey(host))
        {
            // Rewrite host cookie index
            editor.putString(host, TextUtils.join(",", cookies.get(host).keySet()));
        }
        else
        {
            // No cookies left for host
            editor.remove(host);
        }

        editor.apply();
        return true;
    }
    catch (ClassCastException e)
    {
        Log.d(LOG_TAG, "ClassCastException in remove", e);
        return false;
    }
}