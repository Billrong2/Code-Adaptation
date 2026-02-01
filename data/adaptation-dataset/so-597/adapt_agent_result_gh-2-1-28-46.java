public static Map<String, Object> toMap(final JSONObject object) throws JSONException {
        final Map<String, Object> map = new HashMap<String, Object>();
        if (object == null || object == JSONObject.NULL) {
            return map;
        }
        final Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            final String key = keysItr.next();
            Object value = object.get(key);
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