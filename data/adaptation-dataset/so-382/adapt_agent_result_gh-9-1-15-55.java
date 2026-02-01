    /**
     * Get the value for the given key.
     * @param def default value to return if the key isn't found or an error occurs
     * @return the system property value, or {@code def} (or empty string if {@code def} is null)
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    private static String get(android.content.Context context, String key, String def) throws IllegalArgumentException {
        final String fallback = (def != null) ? def : "";
        String ret = fallback;

        try {
            if (context == null) {
                return fallback;
            }

            final ClassLoader cl = context.getClassLoader();
            final Class<?> systemProperties = cl.loadClass("android.os.SystemProperties");

            final Class<?>[] paramTypes = new Class<?>[] { String.class, String.class };
            final java.lang.reflect.Method getMethod = systemProperties.getMethod("get", paramTypes);

            final Object[] params = new Object[] { key, def };
            final Object value = getMethod.invoke(null, params);
            if (value instanceof String) {
                ret = (String) value;
            }
        } catch (IllegalArgumentException iAE) {
            throw iAE;
        } catch (Exception e) {
            return fallback;
        }

        return ret;
    }