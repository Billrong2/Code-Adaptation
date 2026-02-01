public PersistentCookieStore(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null");
        }
        // Initialize persistent preferences backing the cookie store
        mCookiePrefs = context.getSharedPreferences(COOKIE_PREFS, 0);
        // In-memory cookie map keyed by host
        mCookieMap = new HashMap<String, ConcurrentHashMap<String, HttpCookie>>();

        // Load any previously stored cookies into the in-memory cookie map
        final Map<String, ?> prefsMap = mCookiePrefs.getAll();
        if (prefsMap == null || prefsMap.isEmpty()) {
            return;
        }
        for (final Map.Entry<String, ?> entry : prefsMap.entrySet()) {
            final Object value = entry.getValue();
            if (!(value instanceof String)) {
                continue; // Guard against unexpected preference value types
            }
            final String storedValue = (String) value;
            if (storedValue != null && !storedValue.startsWith(COOKIE_NAME_PREFIX)) {
                final String[] cookieNames = TextUtils.split(storedValue, ",");
                for (final String name : cookieNames) {
                    final String encodedCookie = mCookiePrefs.getString(COOKIE_NAME_PREFIX + name, null);
                    if (encodedCookie != null) {
                        final HttpCookie decodedCookie = decodeCookie(encodedCookie);
                        if (decodedCookie != null) {
                            if (!mCookieMap.containsKey(entry.getKey())) {
                                mCookieMap.put(entry.getKey(), new ConcurrentHashMap<String, HttpCookie>());
                            }
                            mCookieMap.get(entry.getKey()).put(name, decodedCookie);
                        }
                    }
                }
            }
        }
    }