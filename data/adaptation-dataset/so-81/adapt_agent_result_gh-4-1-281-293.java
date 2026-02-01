private static int deleteValue(Preferences root, int hkey, String key, String value, int wow64)
        throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
    if (root == null) {
        throw new IllegalArgumentException("root is null");
    }
    if (key == null) {
        throw new IllegalArgumentException("key is null");
    }
    if (value == null) {
        throw new IllegalArgumentException("value is null");
    }

    int[] handles = null;
    int rc;
    try {
        handles = (int[]) regOpenKey.invoke(root,
                new Object[] { Integer.valueOf(hkey), toCstr(key), Integer.valueOf(KEY_ALL_ACCESS | wow64) });
        if (handles[1] != REG_SUCCESS) {
            // propagate open-key failure code
            return handles[1];
        }
        rc = ((Integer) regDeleteValue.invoke(root,
                new Object[] { Integer.valueOf(handles[0]), toCstr(value) })).intValue();
        return rc; // REG_SUCCESS, REG_NOTFOUND, REG_ACCESSDENIED, etc.
    } finally {
        if (handles != null && handles.length > 0 && handles[0] != 0) {
            try {
                regCloseKey.invoke(root, new Object[] { Integer.valueOf(handles[0]) });
            } catch (Exception ignore) {
                // best-effort close; do not mask original result
            }
        }
    }
}