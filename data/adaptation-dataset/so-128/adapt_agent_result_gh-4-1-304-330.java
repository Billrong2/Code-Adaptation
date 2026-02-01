private static Map<String, String> readStringValues(Preferences root, int hkey, String key)
        throws IllegalArgumentException, IllegalAccessException,
        InvocationTargetException {
        Map<String, String> results = new HashMap<>();
        int[] handles = (int[]) regOpenKey.invoke(root, hkey, toCstr(key), KEY_READ);
        if (handles == null || handles.length < 2 || handles[1] != REG_SUCCESS) {
            return null;
        }
        int handle = handles[0];
        try {
            int[] info = (int[]) regQueryInfoKey.invoke(root, handle);
            if (info == null || info.length < 3) {
                return results;
            }
            int count = info[2]; // number of values
            if (count <= 0) {
                return results;
            }
            final int MAX_VALUE_NAME_LENGTH = 256;
            for (int index = 0; index < count; index++) {
                byte[] nameBytes = (byte[]) regEnumValue.invoke(root, handle, index, MAX_VALUE_NAME_LENGTH + 1);
                if (nameBytes == null) {
                    continue;
                }
                String valueName = new String(nameBytes).trim();
                if (valueName.isEmpty()) {
                    continue;
                }
                String value = readString(root, hkey, key, valueName);
                results.put(valueName, value);
            }
            return results;
        } finally {
            regCloseKey.invoke(root, handle);
        }
    }