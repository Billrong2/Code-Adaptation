private static List<String> readStringSubKeys(Preferences root, int hkey, String key, int wow64)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if (root == null || key == null) {
            throw new IllegalArgumentException("root/key must not be null");
        }
        List<String> results = new ArrayList<String>();
        int[] handles = (int[]) regOpenKey.invoke(root,
            new Object[] { Integer.valueOf(hkey), toCstr(key), Integer.valueOf(KEY_READ | wow64) });
        if (handles[1] != REG_SUCCESS) {
            return null;
        }
        try {
            int[] info = (int[]) regQueryInfoKey.invoke(root, new Object[] { Integer.valueOf(handles[0]) });
            int count = info[0]; // subkey count (correct index)
            int maxlen = info[3]; // max subkey name length (correct index)
            if (count <= 0) {
                return results;
            }
            for (int index = 0; index < count; index++) {
                byte[] name = (byte[]) regEnumKeyEx.invoke(root,
                    new Object[] { Integer.valueOf(handles[0]), Integer.valueOf(index), Integer.valueOf(maxlen + 1) });
                if (name == null) {
                    continue;
                }
                String stringValue = convertByteToUTF8String(name);
                if (stringValue != null) {
                    results.add(stringValue.trim());
                }
            }
            return results;
        } finally {
            regCloseKey.invoke(root, new Object[] { Integer.valueOf(handles[0]) });
        }
    }