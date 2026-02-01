private static String readString(Preferences root, int hkey, String key, String valueName)
        throws IllegalAccessException, InvocationTargetException {
    int handle = -1;
    try {
        int[] handles = (int[]) regOpenKey.invoke(root, hkey, toCstr(key), KEY_READ);
        if (handles == null || handles.length < 2 || handles[1] != REG_SUCCESS) {
            return null;
        }
        handle = handles[0];
        byte[] data = (byte[]) regQueryValueEx.invoke(root, handle, toCstr(valueName));
        if (data == null || data.length == 0) {
            return null;
        }
        String result = new String(data).trim();
        return result.isEmpty() ? null : result;
    } finally {
        if (handle != -1) {
            try {
                regCloseKey.invoke(root, handle);
            } catch (Exception e) {
                // ignore close failures
            }
        }
    }
}