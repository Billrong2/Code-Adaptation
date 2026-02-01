private static void writeStringValue(Preferences root, int hkey, String key, String valueName, String value, int wow64)
            throws IllegalAccessException, InvocationTargetException {
        if (root == null) {
            throw new IllegalArgumentException("root must not be null");
        }
        if (key == null || key.length() == 0) {
            throw new IllegalArgumentException("key must not be null or empty");
        }
        if (valueName == null) {
            throw new IllegalArgumentException("valueName must not be null");
        }
        if (value == null) {
            throw new IllegalArgumentException("value must not be null");
        }

        int[] handles = null;
        try {
            handles = (int[]) regOpenKey.invoke(root,
                    new Object[] { Integer.valueOf(hkey), toCstr(key), Integer.valueOf(KEY_ALL_ACCESS | wow64) });
            // Intentionally do not check return codes; rely on reflection-related exceptions only
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