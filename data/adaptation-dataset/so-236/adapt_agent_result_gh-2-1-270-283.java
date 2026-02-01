private static int deleteValue(Preferences root, int hkey, String key, String value) {
        if (root == null || key == null || key.isEmpty() || value == null || value.isEmpty()) {
            return REG_NOTFOUND;
        }
        int handle = 0;
        boolean opened = false;
        try {
            int[] handles = (int[]) regOpenKey.invoke(root, hkey, toCstr(key), KEY_ALL_ACCESS);
            if (handles == null || handles.length < 2) {
                return REG_NOTFOUND;
            }
            if (handles[1] != REG_SUCCESS) {
                return handles[1];
            }
            handle = handles[0];
            opened = true;
            Integer rc = (Integer) regDeleteValue.invoke(root, handle, toCstr(value));
            return rc != null ? rc : REG_NOTFOUND;
        } catch (RuntimeException e) {
            return REG_ACCESSDENIED;
        } catch (Exception e) {
            return REG_ACCESSDENIED;
        } finally {
            if (opened) {
                try {
                    regCloseKey.invoke(root, handle);
                } catch (Exception ignore) {
                    // ignore close failures
                }
            }
        }
    }