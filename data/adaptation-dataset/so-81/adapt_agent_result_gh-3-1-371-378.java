private static void writeStringValue(Preferences root, int hkey, String key, String valueName, String value, int wow64)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if (root == null || key == null || valueName == null || value == null) {
            throw new IllegalArgumentException("root/key/valueName/value must not be null");
        }
        int[] handles = null;
        try {
            handles = (int[]) regOpenKey.invoke(root,
                new Object[] { Integer.valueOf(hkey), toCstr(key), Integer.valueOf(KEY_ALL_ACCESS | wow64) });
            // Do not check success status or return values as per write-only contract
            regSetValueEx.invoke(root,
                new Object[] { Integer.valueOf(handles[0]), toCstr(valueName), toCstr(value) });
        } finally {
            if (handles != null) {
                regCloseKey.invoke(root, new Object[] { Integer.valueOf(handles[0]) });
            }
        }
    }