private static String readString(Preferences root, int hkey, String key, String valueName, int wow64) {
        int[] handles = null;
        try {
            handles = (int[]) regOpenKey.invoke(root,
                    new Object[] { Integer.valueOf(hkey), toCstr(key), Integer.valueOf(KEY_READ | wow64) });
            if (handles == null || handles.length < 2 || handles[1] != REG_SUCCESS) {
                return null;
            }
            byte[] data = (byte[]) regQueryValueEx.invoke(root,
                    new Object[] { Integer.valueOf(handles[0]), toCstr(valueName) });
            if (data == null || data.length == 0) {
                return null;
            }
            String value = convertByteToUTF8String(data);
            if (value == null) {
                return null;
            }
            value = value.replace("\u0000", "").trim();
            return value.isEmpty() ? null : value;
        } catch (IllegalAccessException e) {
            return null;
        } catch (InvocationTargetException e) {
            return null;
        } finally {
            if (handles != null && handles.length > 0 && handles[0] != 0) {
                try {
                    regCloseKey.invoke(root, new Object[] { Integer.valueOf(handles[0]) });
                } catch (Exception ignore) {
                    // ignore cleanup failures
                }
            }
        }
    }