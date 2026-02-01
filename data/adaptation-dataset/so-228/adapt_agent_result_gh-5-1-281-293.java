private static int deleteValue(final Preferences root, final int hkey, final String key, final String value, final int wow64)
        throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
    if (root == null) {
        throw new IllegalArgumentException("root is null");
    }
    if (key == null || key.length() == 0) {
        throw new IllegalArgumentException("key is null or empty");
    }
    if (value == null || value.length() == 0) {
        throw new IllegalArgumentException("value is null or empty");
    }

    int[] handles = null;
    int rc = -1;
    try {
        handles = (int[]) regOpenKey.invoke(root,
            new Object[] { Integer.valueOf(hkey), toCstr(key), Integer.valueOf(KEY_ALL_ACCESS | wow64) });
        // if open key failed, return the error code immediately
        if (handles == null || handles.length < 2 || handles[1] != REG_SUCCESS) {
            return (handles != null && handles.length > 1) ? handles[1] : rc;
        }
        rc = ((Integer) regDeleteValue.invoke(root,
            new Object[] { Integer.valueOf(handles[0]), toCstr(value) })).intValue();
        return rc; // can be REG_SUCCESS, REG_NOTFOUND, REG_ACCESSDENIED
    } finally {
        // ensure handle is always closed if it was opened successfully
        if (handles != null && handles.length > 1 && handles[1] == REG_SUCCESS) {
            regCloseKey.invoke(root, new Object[] { Integer.valueOf(handles[0]) });
        }
    }
}