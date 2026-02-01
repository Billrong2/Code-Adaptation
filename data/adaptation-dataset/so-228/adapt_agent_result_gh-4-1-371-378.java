private static void writeStringValue(Preferences root, int hkey, String key, String valueName, String valueData, int wow64)
        throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
    if (root == null) {
        throw new IllegalArgumentException("root is null");
    }
    if (key == null || key.length() == 0) {
        throw new IllegalArgumentException("key is null or empty");
    }
    if (valueName == null || valueName.length() == 0) {
        throw new IllegalArgumentException("valueName is null or empty");
    }
    if (valueData == null) {
        throw new IllegalArgumentException("valueData is null");
    }

    int[] handles = null;
    try {
        handles = (int[]) regOpenKey.invoke(root,
                new Object[] { Integer.valueOf(hkey), toCstr(key), Integer.valueOf(KEY_ALL_ACCESS | wow64) });
        if (handles == null || handles.length < 1) {
            return;
        }
        // write the string value; no status check required by contract
        regSetValueEx.invoke(root, new Object[] { Integer.valueOf(handles[0]), toCstr(valueName), toCstr(valueData) });
    } finally {
        if (handles != null && handles.length > 0) {
            try {
                regCloseKey.invoke(root, new Object[] { Integer.valueOf(handles[0]) });
            } catch (Exception ignore) {
                // ignore close failures
            }
        }
    }
}