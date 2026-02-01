private static String get(Context context, String key, String def) throws IllegalArgumentException {
        /**
         * Get the value for the given key.
         * @return the provided default value if the key isn't found or on non-IllegalArgumentException errors
         * @throws IllegalArgumentException if the key exceeds 32 characters (unchanged behavior)
         * Source: android.os.SystemProperties#get(String, String) via reflection
         */
        String ret = def;

        if (context == null || key == null) {
            return def;
        }

        try {
            final ClassLoader cl = context.getClassLoader();
            @SuppressWarnings("rawtypes")
            final Class systemProperties = cl.loadClass("android.os.SystemProperties");

            @SuppressWarnings("rawtypes")
            final Class[] paramTypes = new Class[2];
            paramTypes[0] = String.class;
            paramTypes[1] = String.class;

            final Method getMethod = systemProperties.getMethod("get", paramTypes);

            final Object[] params = new Object[2];
            params[0] = key;
            params[1] = def;

            ret = (String) getMethod.invoke(systemProperties, params);
        } catch (IllegalArgumentException iAE) {
            throw iAE;
        } catch (Exception e) {
            // keep default value on any non-IllegalArgumentException
            ret = def;
        }

        return ret;
    }