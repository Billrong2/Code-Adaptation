private static Map<String, Object> toMap(JSONObject object) throws JSONException {
        if (object == null)
            return new HashMap<>(0);

        final int size = object.length();
        final Map<String, Object> map = new HashMap<>(size);
        final Set<String> keys = object.keySet();

        for (String key : keys) {
            Object value = object.get(key);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }