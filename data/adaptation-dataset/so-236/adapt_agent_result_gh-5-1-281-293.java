private static int deleteValue(Preferences root, int hkey, String key, String value, int wow64) {
    // Validate inputs
    if (root == null || key == null || key.length() == 0 || value == null || value.length() == 0) {
        return REG_NOTFOUND;
    }
    int[] handles = null;
    int rc = REG_NOTFOUND;
    try {
        Object opened = regOpenKey.invoke(root,
            new Object[] { Integer.valueOf(hkey), toCstr(key), Integer.valueOf(KEY_ALL_ACCESS | wow64) });
        if (!(opened instanceof int[])) {
            return REG_ACCESSDENIED;
        }
        handles = (int[]) opened;
        if (handles.length < 2) {
            return REG_ACCESSDENIED;
        }
        rc = handles[1];
        if (rc != REG_SUCCESS) {
            return rc;
        }
        Object deleted = regDeleteValue.invoke(root,
            new Object[] { Integer.valueOf(handles[0]), toCstr(value) });
        if (deleted instanceof Integer) {
            return ((Integer) deleted).intValue();
        }
        return REG_ACCESSDENIED;
    } catch (Exception e) {
        // Map all failures to registry-style error codes
        return REG_ACCESSDENIED;
    } finally {
        if (handles != null && handles.length > 0 && handles[0] != 0) {
            try {
                regCloseKey.invoke(root, new Object[] { Integer.valueOf(handles[0]) });
            } catch (Exception ignore) {
                // ignore close failures
            }
        }
    }
  }