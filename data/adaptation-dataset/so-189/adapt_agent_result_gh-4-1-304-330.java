private static Map<String, String> readStringValues(Preferences root, int hkey, String key)
        throws IllegalArgumentException, IllegalAccessException,
        InvocationTargetException {
    final int MAX_VALUE_NAME_LENGTH = 256;
    Map<String, String> results = new HashMap<>();
    int[] handles = null;
    try {
        handles = (int[]) regOpenKey.invoke(root, hkey, toCstr(key), KEY_READ);
        if (handles == null || handles[1] != REG_SUCCESS) {
            return null;
        }
        int[] info = (int[]) regQueryInfoKey.invoke(root, handles[0]);
        if (info == null || info.length < 3) {
            return results;
        }
        int valueCount = info[2];
        for (int index = 0; index < valueCount; index++) {
            byte[] nameBytes = (byte[]) regEnumValue.invoke(root, handles[0], index, MAX_VALUE_NAME_LENGTH);
            if (nameBytes == null) {
                continue;
            }
            String valueName = new String(nameBytes).trim();
            if (valueName.isEmpty()) {
                continue;
            }
            String value = readString(root, hkey, key, valueName);
            if (value != null) {
                results.put(valueName.trim(), value.trim());
            }
        }
        return results;
    } finally {
        if (handles != null && handles.length > 0) {
            try {
                regCloseKey.invoke(root, handles[0]);
            } catch (Exception e) {
                // ignore close failures
            }
        }
    }
}