private static String readString(final Preferences root, final int hkey, final String key, final String valueName, final int wow64)
        throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
    if (root == null || key == null || valueName == null) {
        return null;
    }
    int[] handles = null;
    try {
        handles = (int[]) regOpenKey.invoke(root, new Object[] { Integer.valueOf(hkey), toCstr(key), Integer.valueOf(KEY_READ | wow64) });
        if (handles == null || handles.length < 2 || handles[1] != REG_SUCCESS) {
            return null;
        }
        byte[] valueBytes = (byte[]) regQueryValueEx.invoke(root, new Object[] { Integer.valueOf(handles[0]), toCstr(valueName) });
        if (valueBytes == null) {
            return null;
        }
        String decoded = convertByteToUTF8String(valueBytes);
        return decoded != null ? decoded.trim() : null;
    } finally {
        if (handles != null && handles.length > 0) {
            try {
                regCloseKey.invoke(root, new Object[] { Integer.valueOf(handles[0]) });
            } catch (Exception ignore) {
                // ignore cleanup failures
            }
        }
    }
}