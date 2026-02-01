private static void writeStringValue(Preferences root, int hkey, String key, String valueName, String value, int wow64)
        throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
    int[] handles = null;
    try {
        handles = (int[]) regOpenKey.invoke(root,
            new Object[] { Integer.valueOf(hkey), toCstr(key), Integer.valueOf(KEY_ALL_ACCESS | wow64) });
        // Open, set, and close without success/error checking as per intent
        regSetValueEx.invoke(root,
            new Object[] { Integer.valueOf(handles[0]), toCstr(valueName), toCstr(value) });
    } finally {
        if (handles != null) {
            regCloseKey.invoke(root, new Object[] { Integer.valueOf(handles[0]) });
        }
    }
}