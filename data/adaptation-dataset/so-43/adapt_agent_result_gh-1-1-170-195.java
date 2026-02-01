/**
     * Create a Windows registry key.
     *
     * @param hkey registry root, one of {@link #HKEY_CURRENT_USER} or {@link #HKEY_LOCAL_MACHINE}
     * @param key  registry key path to create
     * @throws IllegalArgumentException      if hkey is invalid, key is null/empty, or creation fails (includes rc and key)
     * @throws IllegalAccessException        if the underlying registry call cannot be accessed
     * @throws InvocationTargetException     if the underlying registry call fails
     */
    public static void createKey(int hkey, String key)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("key=" + key);
        }

        final Preferences root;
        if (hkey == HKEY_LOCAL_MACHINE) {
            root = systemRoot;
        } else if (hkey == HKEY_CURRENT_USER) {
            root = userRoot;
        } else {
            throw new IllegalArgumentException("hkey=" + hkey);
        }

        int[] handles = null;
        try {
            handles = createKey(root, hkey, key);
            final int rc = (handles != null && handles.length > 1) ? handles[1] : -1;
            if (rc != REG_SUCCESS) {
                throw new IllegalArgumentException("rc=" + rc + "  key=" + key);
            }
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