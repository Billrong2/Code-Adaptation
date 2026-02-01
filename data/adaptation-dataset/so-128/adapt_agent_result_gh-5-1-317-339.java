private static Map<String, String> readStringValues(Preferences root, int hkey, String key, int wow64)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if (root == null || key == null) {
            return null;
        }
        Map<String, String> results = new HashMap<String, String>();
        int[] handles = null;
        try {
            handles = (int[]) regOpenKey.invoke(root,
                new Object[] { Integer.valueOf(hkey), toCstr(key), Integer.valueOf(KEY_READ | wow64) });
            if (handles == null || handles[1] != REG_SUCCESS) {
                return null;
            }
            int hKeyHandle = handles[0];
            int[] info = (int[]) regQueryInfoKey.invoke(root, new Object[] { Integer.valueOf(hKeyHandle) });
            if (info == null) {
                return results;
            }
            int valueCount = info[0];
            int maxValueNameLen = info[3];
            for (int index = 0; index < valueCount; index++) {
                byte[] nameBytes = (byte[]) regEnumValue.invoke(root,
                    new Object[] { Integer.valueOf(hKeyHandle), Integer.valueOf(index), Integer.valueOf(maxValueNameLen + 1) });
                if (nameBytes == null) {
                    continue;
                }
                String valueName = convertByteToUTF8String(nameBytes);
                if (valueName == null) {
                    continue;
                }
                String trimmedName = valueName.trim();
                String value = readString(root, hkey, key, trimmedName, wow64);
                results.put(trimmedName, value);
            }
            return results;
        } finally {
            if (handles != null && handles.length > 0 && handles[1] == REG_SUCCESS) {
                try {
                    regCloseKey.invoke(root, new Object[] { Integer.valueOf(handles[0]) });
                } catch (Exception e) {
                    // ignore close failures
                }
            }
        }
    }