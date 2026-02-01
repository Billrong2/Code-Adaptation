public static Boolean getBoolean(final ClassLoader classLoader, final String key, final boolean def) throws IllegalArgumentException {
    boolean result = def;
    try {
      @SuppressWarnings("rawtypes")
      Class systemProperties = classLoader.loadClass("android.os.SystemProperties");

      @SuppressWarnings("rawtypes")
      Class[] paramTypes = new Class[] { String.class, boolean.class };
      Method getBooleanMethod = systemProperties.getMethod("getBoolean", paramTypes);

      Object[] params = new Object[] { key, Boolean.valueOf(def) };
      Object value = getBooleanMethod.invoke(systemProperties, params);
      if (value instanceof Boolean) {
        result = ((Boolean) value).booleanValue();
      }
    } catch (IllegalArgumentException iAE) {
      throw iAE;
    } catch (Exception e) {
      Log.e(TAG, "Failed to get boolean system property. key=" + key + ", def=" + def, e);
      result = def;
    }
    return Boolean.valueOf(result);
  }