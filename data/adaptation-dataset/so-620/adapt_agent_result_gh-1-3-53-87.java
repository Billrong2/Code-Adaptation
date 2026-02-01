public PersistentCookieStore(Context context)
{
    cookiePrefs = context != null ? context.getSharedPreferences(COOKIE_PREFS, 0) : null;
    cookies = new HashMap<String, ConcurrentHashMap<String, Cookie>>();

    if (cookiePrefs == null)
        return;

    // Load any previously stored cookies into the store
    Map<String, ?> prefsMap = cookiePrefs.getAll();
    if (prefsMap == null)
        return;

    for (Map.Entry<String, ?> entry : prefsMap.entrySet())
    {
        Object value = entry.getValue();
        if (!(value instanceof String))
            continue;

        String storedNames = (String) value;
        if (TextUtils.isEmpty(storedNames) || storedNames.startsWith(COOKIE_NAME_PREFIX))
            continue;

        String[] cookieNames = TextUtils.split(storedNames, ",");
        for (String name : cookieNames)
        {
            String encodedCookie = cookiePrefs.getString(COOKIE_NAME_PREFIX + name, null);
            if (encodedCookie == null)
                continue;

            Cookie decodedCookie = decodeCookie(encodedCookie);
            if (decodedCookie == null)
                continue;

            // Basic validation for OkHttp Cookie
            if (TextUtils.isEmpty(decodedCookie.domain()) || TextUtils.isEmpty(decodedCookie.name()))
                continue;

            if (!cookies.containsKey(entry.getKey()))
                cookies.put(entry.getKey(), new ConcurrentHashMap<String, Cookie>());

            cookies.get(entry.getKey()).put(name, decodedCookie);
        }
    }
}