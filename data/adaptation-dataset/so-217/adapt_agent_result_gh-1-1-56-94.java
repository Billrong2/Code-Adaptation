public static String propertyGet(Context context, String key) throws IllegalArgumentException {
    // Reference: https://stackoverflow.com/questions/2638161/access-system-properties-on-android
    String ret = "";

    try {
        ClassLoader cl = context.getClassLoader();
        @SuppressWarnings("rawtypes")
        Class SystemProperties = cl.loadClass("android.os.SystemProperties");

        // Parameters Types
        @SuppressWarnings("rawtypes")
        Class[] paramTypes = new Class[1];
        paramTypes[0] = String.class;

        Method get = SystemProperties.getMethod("get", paramTypes);

        // Parameters
        Object[] params = new Object[1];
        params[0] = new String(key);

        ret = (String) get.invoke(SystemProperties, params);
    } catch (IllegalArgumentException iAE) {
        throw iAE;
    } catch (Exception e) {
        ret = "";
        // TODO
    }

    return ret;
}