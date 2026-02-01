private static int deleteValue(Preferences root, int hkey, String key, String value, int wow64)
        throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        // minimal input validation
        if (root == null || key == null || value == null) {
            return REG_NOTFOUND;
        }
        int[] handles = null;
        int rc = REG_NOTFOUND;
        try {
            handles = (int[]) regOpenKey.invoke(root,
                    new Object[] { Integer.valueOf(hkey), toCstr(key), Integer.valueOf(KEY_ALL_ACCESS | wow64) });
            if (handles == null || handles.length < 2) {
                return REG_NOTFOUND;
            }
            if (handles[1] != REG_SUCCESS) {
                // propagate open-key failure status
                return handles[1];
            }
            // delete the value and return its status code
            rc = ((Integer) regDeleteValue.invoke(root,
                    new Object[] { Integer.valueOf(handles[0]), toCstr(value) })).intValue();
            return rc;
        } finally {
            // ensure the key handle is always closed if it was opened
            if (handles != null && handles.length > 1 && handles[1] == REG_SUCCESS) {
                regCloseKey.invoke(root, new Object[] { Integer.valueOf(handles[0]) });
            }
        }
    }