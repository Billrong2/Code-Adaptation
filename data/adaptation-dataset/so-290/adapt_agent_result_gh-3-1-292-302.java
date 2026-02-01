private static String readString(final Preferences root, final int hkey, final String key, final String valueName)
        throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
    if (root == null || key == null || valueName == null) {
        return null;
    }
    int[] handles = null;
    int handle = 0;
    boolean opened = false;
    try {
        handles = (int[]) regOpenKey.invoke(root, hkey, toCstr(key), KEY_READ);
        if (handles == null || handles.length < 2 || handles[1] != REG_SUCCESS) {
            return null;
        }
        handle = handles[0];
        opened = true;
        byte[] valb = (byte[]) regQueryValueEx.invoke(root, handle, toCstr(valueName));
        return (valb != null ? new String(valb).trim() : null);
    } finally {
        if (opened) {
            regCloseKey.invoke(root, handle);
        }
    }
}