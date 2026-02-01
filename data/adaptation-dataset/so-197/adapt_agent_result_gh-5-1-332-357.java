private static List<String> readStringSubKeys(Preferences root, int hkey, String key)
        throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        List<String> subKeys = new ArrayList<>();
        int[] keyHandles = (int[]) regOpenKey.invoke(root, hkey, toCstr(key), KEY_READ);
        if (keyHandles == null || keyHandles.length < 2 || keyHandles[1] != REG_SUCCESS) {
            return null;
        }
        int hKeyHandle = keyHandles[0];
        try {
            int[] info = (int[]) regQueryInfoKey.invoke(root, hKeyHandle);
            // info layout is implementation-specific; defensively derive max name length
            int maxNameLen = 256;
            if (info != null && info.length > 3 && info[3] > 0) {
                maxNameLen = info[3] + 1;
            }
            for (int index = 0; ; index++) {
                byte[] nameBytes = (byte[]) regEnumKeyEx.invoke(root, hKeyHandle, index, maxNameLen);
                if (nameBytes == null) {
                    break; // no more entries
                }
                String name = new String(nameBytes).trim();
                if (!name.isEmpty()) {
                    subKeys.add(name);
                }
            }
        } finally {
            // always close the registry key handle
            regCloseKey.invoke(root, hKeyHandle);
        }
        return subKeys;
    }