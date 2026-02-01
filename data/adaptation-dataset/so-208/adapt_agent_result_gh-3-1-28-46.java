public static Map<String, Object> toMap(final JSONObject json) throws JSONException {
        final Map<String, Object> map = new HashMap<>();
        if (json == null || json == JSONObject.NULL) {
            return map;
        }

        final Iterator<String> keysItr = json.keys();
        while (keysItr.hasNext()) {
            final String key = keysItr.next();
            Object value = json.get(key);

            if (value == JSONObject.NULL) {
                value = null;
            } else if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }