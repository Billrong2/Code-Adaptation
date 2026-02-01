public PersistentCookieStore(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null");
        }

        this.cookiePrefs = context.getSharedPreferences(COOKIE_PREFS, 0);
        this.cookies = new HashMap<String, ConcurrentHashMap<String, HttpCookie>>();

        if (cookiePrefs == null) {
            return;
        }

        Map<String, ?> prefsMap = cookiePrefs.getAll();
        if (prefsMap == null || prefsMap.isEmpty()) {
            return;
        }

        for (Map.Entry<String, ?> entry : prefsMap.entrySet()) {
            Object value = entry.getValue();
            if (!(value instanceof String)) {
                continue;
            }

            String storedValue = (String) value;
            if (storedValue == null || storedValue.startsWith(COOKIE_NAME_PREFIX)) {
                continue;
            }

            String[] cookieNames = TextUtils.split(storedValue, ",");
            if (cookieNames == null || cookieNames.length == 0) {
                continue;
            }

            for (String name : cookieNames) {
                String encodedCookie = cookiePrefs.getString(COOKIE_NAME_PREFIX + name, null);
                if (encodedCookie == null) {
                    continue;
                }

                HttpCookie decodedCookie;
                try {
                    decodedCookie = decodeCookie(encodedCookie);
                } catch (Exception e) {
                    Log.d(LOG_TAG, "Exception in decodeCookie", e);
                    continue;
                }

                if (decodedCookie == null) {
                    continue;
                }

                String host = entry.getKey();
                if (!cookies.containsKey(host)) {
                    cookies.put(host, new ConcurrentHashMap<String, HttpCookie>());
                }
                cookies.get(host).put(name, decodedCookie);
            }
        }
    }