private static String readString(Preferences root, int hkey, String key, String valueName)
        throws IllegalAccessException, InvocationTargetException {
    if (root == null || key == null || valueName == null) {
        return null;
    }

    int[] handles = null;
    try {
        handles = (int[]) regOpenKey.invoke(root, hkey, toCstr(key), KEY_READ);
        if (handles == null || handles.length < 2 || handles[1] != REG_SUCCESS) {
            return null;
        }

        byte[] data = (byte[]) regQueryValueEx.invoke(root, handles[0], toCstr(valueName));
        if (data == null || data.length == 0) {
            return null;
        }

        String value = new String(data).trim();
        return value.isEmpty() ? null : value;
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