private static List<String> readStringSubKeys(Preferences root, int hkey, String keyName, int wow64)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if (root == null || keyName == null) {
            return null;
        }
        if (wow64 != 0 && wow64 != KEY_WOW64_32KEY && wow64 != KEY_WOW64_64KEY) {
            throw new IllegalArgumentException("Invalid WOW64 flag: " + wow64);
        }
        List<String> results = new ArrayList<String>();
        int[] handleInfo = null;
        int handle = -1;
        try {
            handleInfo = (int[]) regOpenKey.invoke(root,
                new Object[] { Integer.valueOf(hkey), toCstr(keyName), Integer.valueOf(KEY_READ | wow64) });
            if (handleInfo == null || handleInfo[1] != REG_SUCCESS) {
                return null;
            }
            handle = handleInfo[0];
            int[] info = (int[]) regQueryInfoKey.invoke(root, new Object[] { Integer.valueOf(handle) });
            int count = info[0]; // number of subkeys
            int maxlen = info[3]; // maximum subkey name length
            for (int index = 0; index < count; index++) {
                byte[] nameBytes = (byte[]) regEnumKeyEx.invoke(root,
                    new Object[] { Integer.valueOf(handle), Integer.valueOf(index), Integer.valueOf(maxlen + 1) });
                if (nameBytes != null) {
                    String name = convertByteToUTF8String(nameBytes);
                    if (name != null) {
                        results.add(name.trim());
                    }
                }
            }
            return results;
        } finally {
            if (handle != -1) {
                try {
                    regCloseKey.invoke(root, new Object[] { Integer.valueOf(handle) });
                } catch (Exception e) {
                    // ignore close failures
                }
            }
        }
    }