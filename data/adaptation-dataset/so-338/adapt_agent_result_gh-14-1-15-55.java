    /**
     * Get the value for the given system property key.
     * @param context Android context used to load SystemProperties
     * @param key system property key (must be <= 32 characters)
     * @param defaultValue value to return when the key is missing or on non-IllegalArgumentException failures
     * @return the system property value, or {@code defaultValue} when unavailable
     * @throws IllegalArgumentException if the key is invalid
     */
    private static String get(android.content.Context context, String key, String defaultValue) throws IllegalArgumentException {
        // Initialize to provided default
        String value = defaultValue;

        if (key == null) {
            throw new IllegalArgumentException("key == null");
        }
        if (key.length() > 32) {
            throw new IllegalArgumentException("key length > 32");
        }
        if (context == null) {
            return value;
        }

        try {
            ClassLoader cl = context.getClassLoader();
            Class<?> systemProperties = cl.loadClass("android.os.SystemProperties");
            java.lang.reflect.Method getMethod = systemProperties.getMethod("get", String.class, String.class);
            Object result = getMethod.invoke(systemProperties, key, defaultValue);
            if (result instanceof String) {
                value = (String) result;
            }
        } catch (IllegalArgumentException iAE) {
            // Propagate per contract
            throw iAE;
        } catch (Exception e) {
            // Fallback to defaultValue on any reflection or runtime failure
            value = defaultValue;
        }

        return value;
    }