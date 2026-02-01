    /**
     * Get the value for the given system property key.
     * @param context Android context used to obtain the class loader
     * @param key system property key
     * @param def default value to return if the key is missing or an error occurs
     * @return the system property value, or {@code def} if unavailable
     * @throws IllegalArgumentException if the key exceeds the allowed length
     */
    private static String get(Context context, String key, String def) throws IllegalArgumentException {
        String result = def;
        if (context == null || key == null) {
            return def;
        }
        try {
            ClassLoader classLoader = context.getClassLoader();
            Class<?> systemPropertiesClass = classLoader.loadClass("android.os.SystemProperties");
            Class<?>[] paramTypes = new Class<?>[]{String.class, String.class};
            java.lang.reflect.Method getMethod = systemPropertiesClass.getMethod("get", paramTypes);
            Object[] params = new Object[]{key, def};
            Object value = getMethod.invoke(systemPropertiesClass, params);
            if (value instanceof String) {
                result = (String) value;
            }
        } catch (IllegalArgumentException iAE) {
            throw iAE;
        } catch (Exception e) {
            result = def;
        }
        return result;
    }