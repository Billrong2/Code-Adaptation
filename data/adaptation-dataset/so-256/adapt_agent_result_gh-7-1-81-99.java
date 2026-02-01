public void add(HttpUrl uri, Cookie cookie) {
        if (uri == null || cookie == null || cookiePrefs == null) {
            return;
        }
        String host = uri.host();
        if (host == null || host.length() == 0) {
            return;
        }

        // Determine persistence/expiry
        boolean isExpired = cookie.expiresAt() < System.currentTimeMillis();
        boolean shouldKeep = cookie.persistent() && !isExpired;

        // Ensure host map
        if (!cookies.containsKey(host)) {
            cookies.put(host, new ConcurrentHashMap<String, Cookie>());
        }

        // Generate a unique token per cookie
        String token = java.util.UUID.randomUUID().toString();

        SharedPreferences.Editor editor = cookiePrefs.edit();
        try {
            if (shouldKeep) {
                // Upsert in-memory
                cookies.get(host).put(token, cookie);

                // Update index for this host
                String index = cookiePrefs.getString(host, "");
                java.util.ArrayList<String> tokens = new java.util.ArrayList<String>();
                if (!TextUtils.isEmpty(index)) {
                    try {
                        for (String t : TextUtils.split(index, ",")) {
                            if (!TextUtils.isEmpty(t)) {
                                tokens.add(t);
                            }
                        }
                    } catch (Exception e) {
                        Log.d(LOG_TAG, "Corrupted cookie index for host: " + host, e);
                        tokens.clear();
                    }
                }
                if (!tokens.contains(token)) {
                    tokens.add(token);
                }

                // Persist index and cookie value
                editor.putString(host, TextUtils.join(",", tokens));
                String encoded = encodeCookie(new SerializableHttpCookie(cookie));
                if (encoded != null) {
                    editor.putString(COOKIE_NAME_PREFIX + token, encoded);
                }
            } else {
                // Remove expired/non-persistent cookie
                if (cookies.containsKey(host)) {
                    cookies.get(host).remove(token);
                }
                editor.remove(COOKIE_NAME_PREFIX + token);

                String index = cookiePrefs.getString(host, "");
                if (!TextUtils.isEmpty(index)) {
                    java.util.ArrayList<String> tokens = new java.util.ArrayList<String>();
                    for (String t : TextUtils.split(index, ",")) {
                        if (!token.equals(t)) {
                            tokens.add(t);
                        }
                    }
                    editor.putString(host, TextUtils.join(",", tokens));
                }
            }
        } catch (ClassCastException e) {
            Log.d(LOG_TAG, "ClassCastException while adding cookie", e);
        } finally {
            editor.commit();
        }
    }