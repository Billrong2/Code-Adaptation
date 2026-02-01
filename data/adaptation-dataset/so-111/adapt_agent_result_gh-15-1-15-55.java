    /**
     * Get the value for the given system property key.
     * <p>
     * Returns the provided default value when the key is missing or when an error occurs.
     * If the provided default is {@code null}, an empty string is returned.
     * </p>
     * <p>
     * Reference: behavior aligned with {@code android.os.SystemProperties#get(String, String)}.
     * </p>
     *
     * @param context Android context used to obtain a ClassLoader
     * @param key system property key
     * @param defaultValue value to return if the key is not found or on failure
     * @return the system property value or the provided default
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    private static String get(Context context, String key, String defaultValue) throws IllegalArgumentException {
        if (key == null || key.length() > 32) {
            throw new IllegalArgumentException("key must be non-null and at most 32 characters");
        }

        final String normalizedDefault = (defaultValue != null) ? defaultValue : "";
        String result = normalizedDefault;

        try {
            ClassLoader classLoader = context.getClassLoader();
            Class<?> systemPropertiesClass = classLoader.loadClass("android.os.SystemProperties");

            Class<?>[] paramTypes = new Class<?>[]{String.class, String.class};
            Method getMethod = systemPropertiesClass.getMethod("get", paramTypes);

            Object[] params = new Object[]{key, normalizedDefault};
            Object value = getMethod.invoke(null, params);
            if (value instanceof String) {
                result = (String) value;
            }
        } catch (IllegalArgumentException iae) {
            throw iae;
        } catch (Exception e) {
            // Fall back to the initialized default value
        }

        return result;
    }