private static Map<String, String> readStringValues(Preferences root, int hkey, String key)
        throws IllegalArgumentException, IllegalAccessException,
        InvocationTargetException {
        final int MAX_VALUE_NAME_LENGTH = 256;
        int[] handles = (int[]) regOpenKey.invoke(root, hkey, toCstr(key), KEY_READ);
        if (handles == null || handles.length < 2 || handles[1] != REG_SUCCESS) {
            return null;
        }
        Map<String, String> results = new HashMap<>();
        try {
            int[] info = (int[]) regQueryInfoKey.invoke(root, handles[0]);
            if (info == null || info.length < 3) {
                return results;
            }
            int valueCount = info[2];
            for (int index = 0; index < valueCount; index++) {
                byte[] nameBytes = (byte[]) regEnumValue.invoke(root, handles[0], index, MAX_VALUE_NAME_LENGTH);
                if (nameBytes == null || nameBytes.length == 0) {
                    continue;
                }
                String valueName = new String(nameBytes).trim();
                if (valueName.isEmpty()) {
                    continue;
                }
                String value = readString(root, hkey, key, valueName);
                if (value != null) {
                    results.put(valueName, value);
                }
            }
            return results;
        } finally {
            regCloseKey.invoke(root, handles[0]);
        }
    }