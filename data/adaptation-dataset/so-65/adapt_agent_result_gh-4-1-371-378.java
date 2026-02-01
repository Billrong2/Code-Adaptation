private static void writeStringValue(final Preferences root, final int hkey, final String key, final String valueName, final String value, final int wow64)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if (root == null || key == null || valueName == null || value == null) {
            throw new IllegalArgumentException("root, key, valueName and value must be non-null");
        }
        int[] handles = null;
        try {
            handles = (int[]) regOpenKey.invoke(root,
                new Object[] { Integer.valueOf(hkey), toCstr(key), Integer.valueOf(KEY_ALL_ACCESS | wow64) });
            // Proceed directly to set the value; exceptions propagate if access is denied or key is invalid
            regSetValueEx.invoke(root, new Object[] { Integer.valueOf(handles[0]), toCstr(valueName), toCstr(value) });
        } finally {
            if (handles != null) {
                try {
                    regCloseKey.invoke(root, new Object[] { Integer.valueOf(handles[0]) });
                } catch (Exception ignore) {
                    // best-effort cleanup
                }
            }
        }
    }