private static int deleteValue(Preferences root, int hkey, String key, String value)
        throws IllegalArgumentException, IllegalAccessException,
        InvocationTargetException {
    int[] handles = null;
    int rc;
    try {
        handles = (int[]) regOpenKey.invoke(root, hkey, toCstr(key), KEY_ALL_ACCESS);
        if (handles == null || handles.length < 2) {
            return REG_NOTFOUND;
        }
        if (handles[1] != REG_SUCCESS) {
            return handles[1];
        }
        Object result = regDeleteValue.invoke(root, handles[0], toCstr(value));
        rc = (result instanceof Integer) ? ((Integer) result).intValue() : REG_SUCCESS;
        return rc;
    } finally {
        if (handles != null && handles.length > 0 && handles[0] != 0) {
            try {
                regCloseKey.invoke(root, handles[0]);
            } catch (Exception e) {
                // ignore close errors
            }
        }
    }
}