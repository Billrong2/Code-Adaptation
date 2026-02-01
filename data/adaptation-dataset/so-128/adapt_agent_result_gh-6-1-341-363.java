private static List<String> readStringSubKeys(Preferences root, int hkey, String key, int wow64)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        List<String> results = new ArrayList<String>();
        int[] handles = (int[]) regOpenKey.invoke(root,
            new Object[] { Integer.valueOf(hkey), toCstr(key), Integer.valueOf(KEY_READ | wow64) });
        if (handles == null || handles.length < 2 || handles[1] != REG_SUCCESS) {
            return null;
        }
        try {
            int[] info = (int[]) regQueryInfoKey.invoke(root, new Object[] { Integer.valueOf(handles[0]) });
            if (info == null || info.length <= 3) {
                return results;
            }
            int subKeyCount = info[0];
            int maxSubKeyLen = info[3];
            if (subKeyCount <= 0 || maxSubKeyLen <= 0) {
                return results;
            }
            for (int index = 0; index < subKeyCount; index++) {
                byte[] name = (byte[]) regEnumKeyEx.invoke(root,
                    new Object[] { Integer.valueOf(handles[0]), Integer.valueOf(index), Integer.valueOf(maxSubKeyLen + 1) });
                if (name != null) {
                    String subKey = convertByteToUTF8String(name);
                    if (subKey != null) {
                        results.add(subKey.trim());
                    }
                }
            }
            return results;
        } finally {
            regCloseKey.invoke(root, new Object[] { Integer.valueOf(handles[0]) });
        }
    }