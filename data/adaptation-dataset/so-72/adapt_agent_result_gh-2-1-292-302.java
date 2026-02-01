private static String readString(final Preferences root, final int hkey, final String key, final String valueName)
        throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
    if (root == null || key == null || valueName == null) {
        return null;
    }
    int[] handles = null;
    try {
        handles = (int[]) regOpenKey.invoke(root, hkey, toCstr(key), KEY_READ);
        if (handles == null || handles.length < 2 || handles[1] != REG_SUCCESS) {
            return null;
        }
        byte[] valb = (byte[]) regQueryValueEx.invoke(root, handles[0], toCstr(valueName));
        return (valb != null ? new String(valb).trim() : null);
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