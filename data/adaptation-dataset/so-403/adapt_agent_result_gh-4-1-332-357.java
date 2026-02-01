private static List<String> readStringSubKeys(Preferences root, int hkey, String key)
        throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        List<String> results = new ArrayList<>();
        int[] handles = (int[]) regOpenKey.invoke(root, hkey, toCstr(key), KEY_READ);
        if (handles == null || handles.length < 2 || handles[1] != REG_SUCCESS) {
            return null;
        }
        int keyHandle = handles[0];
        try {
            int[] info = (int[]) regQueryInfoKey.invoke(root, keyHandle);
            if (info == null || info.length == 0) {
                return results;
            }
            // info[3] is typically the max subkey name length; fall back to a safe default
            int maxNameLen = info.length > 3 && info[3] > 0 ? info[3] : 256;
            for (int index = 0; ; index++) {
                byte[] nameBytes = (byte[]) regEnumKeyEx.invoke(root, keyHandle, index, maxNameLen + 1);
                if (nameBytes == null) {
                    break; // no more entries
                }
                String name = new String(nameBytes).trim();
                if (!name.isEmpty()) {
                    results.add(name);
                }
            }
            return results;
        } finally {
            // Ensure the registry handle is always closed
            regCloseKey.invoke(root, keyHandle);
        }
    }