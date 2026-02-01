public static void createKey(int hkey, String key) throws IllegalArgumentException, IllegalAccessException, java.lang.reflect.InvocationTargetException {
        if (key == null) {
            throw new IllegalArgumentException("key=null");
        }
        int[] ret;
        if (hkey == HKEY_LOCAL_MACHINE) {
            ret = createKey(systemRoot, hkey, key);
            // explicitly box handle to avoid reflective invocation issues
            regCloseKey.invoke(systemRoot, new Object[] { Integer.valueOf(ret[0]) });
        } else if (hkey == HKEY_CURRENT_USER) {
            ret = createKey(userRoot, hkey, key);
            // explicitly box handle to avoid reflective invocation issues
            regCloseKey.invoke(userRoot, new Object[] { Integer.valueOf(ret[0]) });
        } else {
            throw new IllegalArgumentException(HKEY_EQUALS + hkey);
        }
        if (ret[1] != REG_SUCCESS) {
            throw new IllegalArgumentException("rc=" + ret[1] + "  key=" + key);
        }
    }