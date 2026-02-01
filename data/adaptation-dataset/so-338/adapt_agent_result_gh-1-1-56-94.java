public static String propertyGet(Context context, String key) throws IllegalArgumentException {
        // Utility method for retrieving Android system properties via reflection; behavior preserved from original implementation.
        String ret = "";
        try {
            ClassLoader cl = context.getClassLoader();
            @SuppressWarnings("rawtypes")
            Class SystemProperties = cl.loadClass("android.os.SystemProperties");

            @SuppressWarnings("rawtypes")
            Class[] paramTypes = new Class[1];
            paramTypes[0] = String.class;

            java.lang.reflect.Method getMethod = SystemProperties.getMethod("get", paramTypes);

            Object[] params = new Object[1];
            params[0] = new String(key);

            ret = (String) getMethod.invoke(SystemProperties, params);
        } catch (IllegalArgumentException iAE) {
            throw iAE;
        } catch (Exception e) {
            ret = "";
            // TODO
        }
        return ret;
    }