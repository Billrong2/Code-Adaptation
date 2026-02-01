/**
     * Delete a registry value from the specified key.
     * <p>
     * This method deletes an existing value under the given registry key using pure Java access
     * to the Windows Registry. The registry root is selected internally based on {@code hkey}:
     * {@link #HKEY_LOCAL_MACHINE} uses the system registry root, and
     * {@link #HKEY_CURRENT_USER} uses the user registry root.
     * </p>
     * <p>
     * The {@code wow64} parameter controls which registry view is accessed when running on
     * 32-bit/64-bit Windows (for example {@link #KEY_WOW64_32KEY} or {@link #KEY_WOW64_64KEY}).
     * Pass {@code 0} for the default view.
     * </p>
     *
     * @param hkey
     *            HKEY_CURRENT_USER or HKEY_LOCAL_MACHINE
     * @param key
     *            registry key path
     * @param valueName
     *            name of the value to delete
     * @param wow64
     *            0 for default registry view, or KEY_WOW64_32KEY / KEY_WOW64_64KEY to force a view
     * @throws IllegalArgumentException
     *             if parameters are invalid or the delete operation fails
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static void deleteRegistryValue(int hkey, String key, String valueName, int wow64)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if (key == null || key.length() == 0) {
            throw new IllegalArgumentException("key must not be null or empty");
        }
        if (valueName == null || valueName.length() == 0) {
            throw new IllegalArgumentException("valueName must not be null or empty");
        }
        if (wow64 != 0 && wow64 != KEY_WOW64_32KEY && wow64 != KEY_WOW64_64KEY) {
            throw new IllegalArgumentException("unsupported wow64 flag=" + wow64);
        }

        int rc;
        if (hkey == HKEY_LOCAL_MACHINE) {
            rc = deleteValue(systemRoot, hkey, key, valueName, wow64);
        } else if (hkey == HKEY_CURRENT_USER) {
            rc = deleteValue(userRoot, hkey, key, valueName, wow64);
        } else {
            throw new IllegalArgumentException(HKEY_EQUALS + hkey);
        }

        if (rc != REG_SUCCESS) {
            throw new IllegalArgumentException("rc=" + rc + "  key=" + key + "  value=" + valueName);
        }
    }