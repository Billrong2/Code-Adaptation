public static List<Object> toList(final JSONArray array) throws JSONException {
    final List<Object> list = new ArrayList<Object>();

    if (array == null || array == JSONArray.NULL) {
        return list;
    }

    for (int i = 0; i < array.length(); i++) {
        Object value = array.get(i);

        if (value instanceof JSONArray) {
            value = toList((JSONArray) value);
        } else if (value instanceof JSONObject) {
            value = toMap((JSONObject) value);
        }
        // primitives and null are added as-is
        list.add(value);
    }

    return list;
}