public static void createKey(int hkey, String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        final Preferences root;
        if (hkey == HKEY_LOCAL_MACHINE) {
            root = systemRoot;
        } else if (hkey == HKEY_CURRENT_USER) {
            root = userRoot;
        } else {
            throw new IllegalArgumentException(HKEY_EQUALS + hkey);
        }

        int[] handles = null;
        try {
            handles = (int[]) regCreateKeyEx.invoke(root, new Object[] { Integer.valueOf(hkey), toCstr(key) });
            if (handles == null || handles.length < 2 || handles[1] != REG_SUCCESS) {
                final int rc = (handles != null && handles.length >= 2) ? handles[1] : -1;
                throw new IllegalArgumentException("rc=" + rc + "  key=" + key);
            }
        } finally {
            if (handles != null && handles.length > 0) {
                try {
                    regCloseKey.invoke(root, new Object[] { Integer.valueOf(handles[0]) });
                } catch (Exception ignore) {
                    // best-effort cleanup
                }
            }
        }
    }