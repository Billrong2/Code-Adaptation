private static Map<String, String> readStringValues(Preferences root, int hkey, String key, int wow64)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Map<String, String> results = new HashMap<String, String>();
        int[] handles = (int[]) regOpenKey.invoke(root,
                new Object[] { Integer.valueOf(hkey), toCstr(key), Integer.valueOf(KEY_READ | wow64) });
        if (handles == null || handles.length < 2 || handles[1] != REG_SUCCESS) {
            return null;
        }
        int hkeyHandle = handles[0];
        try {
            int[] info = (int[]) regQueryInfoKey.invoke(root, new Object[] { Integer.valueOf(hkeyHandle) });
            if (info == null || info.length < 5) {
                return results;
            }
            int valueCount = info[2]; // number of values
            int maxNameLen = info[4]; // maximum value-name length
            if (valueCount <= 0) {
                return results;
            }
            for (int index = 0; index < valueCount; index++) {
                byte[] nameBytes = (byte[]) regEnumValue.invoke(root,
                        new Object[] { Integer.valueOf(hkeyHandle), Integer.valueOf(index), Integer.valueOf(maxNameLen + 1) });
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
                String value = readString(root, hkey, key, valueName, wow64);
                results.put(valueName, value);
            }
            return results;
        } finally {
            regCloseKey.invoke(root, new Object[] { Integer.valueOf(hkeyHandle) });
        }
    }