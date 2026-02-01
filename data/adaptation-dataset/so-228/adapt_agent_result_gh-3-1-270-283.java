private static int deleteValue(Preferences root, int hkey, String key, String value)
        throws IllegalArgumentException, IllegalAccessException,
        InvocationTargetException {
        if (root == null) {
            throw new IllegalArgumentException("root is null");
        }
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("key is null or empty");
        }
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("value is null or empty");
        }

        int[] handles = (int[]) regOpenKey.invoke(root, hkey, toCstr(key), KEY_ALL_ACCESS);
        if (handles == null || handles.length < 2 || handles[1] != REG_SUCCESS) {
            return handles != null && handles.length > 1 ? handles[1] : REG_NOTFOUND;
        }

        int rc;
        try {
            rc = (Integer) regDeleteValue.invoke(root, handles[0], toCstr(value));
        } finally {
            // always close opened key handle
            regCloseKey.invoke(root, handles[0]);
        }
        return rc;
    }