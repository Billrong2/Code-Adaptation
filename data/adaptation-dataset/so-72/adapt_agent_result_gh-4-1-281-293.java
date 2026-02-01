private static int deleteValue(Preferences root, int hkey, String key, String value, int wow64) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if (root == null || key == null || value == null) {
            throw new IllegalArgumentException("root/key/value must not be null");
        }
        int[] handles = null;
        int rc = -1;
        try {
            handles = (int[]) regOpenKey.invoke(root,
                new Object[] { Integer.valueOf(hkey), toCstr(key), Integer.valueOf(KEY_ALL_ACCESS | wow64) });
            if (handles == null || handles.length < 2) {
                return REG_NOTFOUND;
            }
            if (handles[1] != REG_SUCCESS) {
                return handles[1];
            }
            rc = ((Integer) regDeleteValue.invoke(root,
                new Object[] { Integer.valueOf(handles[0]), toCstr(value) })).intValue();
            return rc;
        } finally {
            if (handles != null && handles.length > 0 && handles[1] == REG_SUCCESS) {
                regCloseKey.invoke(root, new Object[] { Integer.valueOf(handles[0]) });
            }
        }
    }