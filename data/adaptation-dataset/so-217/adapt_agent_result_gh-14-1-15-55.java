private static String get(final android.content.Context context, final String key, final String defaultValue) throws IllegalArgumentException {
    String result = defaultValue;
    if (key == null) {
        return defaultValue;
    }
    try {
        final Class<?> systemPropertiesClass = (context != null)
                ? context.getClassLoader().loadClass("android.os.SystemProperties")
                : Class.forName("android.os.SystemProperties");
        final Class<?>[] paramTypes = new Class<?>[]{String.class, String.class};
        final java.lang.reflect.Method getMethod = systemPropertiesClass.getMethod("get", paramTypes);
        final Object[] params = new Object[]{key, defaultValue};
        result = (String) getMethod.invoke(null, params);
    } catch (IllegalArgumentException iae) {
        throw iae;
    } catch (Exception e) {
        return defaultValue;
    }
    return result;
}