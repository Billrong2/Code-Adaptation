public static void createKey(final int hkey, final String key)
        throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
    if (key == null || key.isEmpty()) {
        throw new IllegalArgumentException("key is null or empty");
    }

    final Preferences root;
    if (hkey == HKEY_LOCAL_MACHINE) {
        root = systemRoot;
    } else if (hkey == HKEY_CURRENT_USER) {
        root = userRoot;
    } else {
        throw new IllegalArgumentException("hkey=" + hkey);
    }

    int[] ret = null;
    try {
        ret = createKey(root, hkey, key);
        if (ret[1] != REG_SUCCESS) {
            throw new IllegalArgumentException("rc=" + ret[1] + "  key=" + key);
        }
    } finally {
        if (ret != null) {
            // ensure native handle is always closed
            regCloseKey.invoke(root, ret[0]);
        }
    }
}