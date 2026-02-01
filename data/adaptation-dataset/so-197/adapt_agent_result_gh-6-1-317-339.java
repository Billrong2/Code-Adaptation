private static Map<String, String> readStringValues(Preferences root, int hkey, String key, int wow64)
        throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        // validate wow64 flag
        if (wow64 != 0 && wow64 != KEY_WOW64_32KEY && wow64 != KEY_WOW64_64KEY) {
            throw new IllegalArgumentException("Invalid wow64 flag: " + wow64);
        }
        Map<String, String> results = new HashMap<String, String>();
        int[] handles = null;
        try {
            handles = (int[]) regOpenKey.invoke(root,
                    new Object[] { Integer.valueOf(hkey), toCstr(key), Integer.valueOf(KEY_READ | wow64) });
            if (handles == null || handles.length < 2 || handles[1] != REG_SUCCESS) {
                return null; // open failed
            }
            int handle = handles[0];
            int[] info = (int[]) regQueryInfoKey.invoke(root, new Object[] { Integer.valueOf(handle) });
            if (info == null || info.length < 5) {
                return results; // unexpected metadata, return empty map
            }
            int valueCount = info[2]; // number of values
            int maxValueNameLen = info[4]; // max value-name length
            if (valueCount <= 0 || maxValueNameLen < 0) {
                return results;
            }
            for (int index = 0; index < valueCount; index++) {
                try {
                    byte[] nameBytes = (byte[]) regEnumValue.invoke(root,
                            new Object[] { Integer.valueOf(handle), Integer.valueOf(index), Integer.valueOf(maxValueNameLen + 1) });
                    if (nameBytes == null) {
                        continue;
                    }
                    String valueName = convertByteToUTF8String(nameBytes);
                    if (valueName == null) {
                        continue;
                    }
                    valueName = valueName.trim();
                    if (valueName.length() == 0) {
                        continue;
                    }
                    // read value using per-name reader to avoid duplication
                    String value = readString(root, hkey, key, valueName, wow64);
                    results.put(valueName, value);
                } catch (Exception e) {
                    // skip individual value failures and continue enumeration
                    continue;
                }
            }
            return results;
        } finally {
            if (handles != null && handles.length > 0) {
                try {
                    regCloseKey.invoke(root, new Object[] { Integer.valueOf(handles[0]) });
                } catch (Exception ignore) {
                    // ignore close failures
                }
            }
        }
    }