private static String readString(final Preferences root, final int hkey, final String key, final String valueName, final int wow64)
        throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if (root == null || key == null || valueName == null) {
            return null;
        }
        int[] handles = null;
        try {
            handles = (int[]) regOpenKey.invoke(root,
                new Object[] { Integer.valueOf(hkey), toCstr(key), Integer.valueOf(KEY_READ | wow64) });
            if (handles == null || handles.length < 2 || handles[1] != REG_SUCCESS) {
                return null;
            }
            byte[] valb = (byte[]) regQueryValueEx.invoke(root,
                new Object[] { Integer.valueOf(handles[0]), toCstr(valueName) });
            return (valb != null ? convertByteToUTF8String(valb).trim() : null);
        } finally {
            if (handles != null && handles.length > 0) {
                try {
                    regCloseKey.invoke(root, new Object[] { Integer.valueOf(handles[0]) });
                } catch (Exception e) {
                    // ignore close failures
                }
            }
        }
    }