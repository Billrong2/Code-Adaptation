public static Boolean getBoolean(final ClassLoader classLoader, final String key, final boolean def)
      throws IllegalArgumentException {
    // Input validation
    if (classLoader == null) {
      Log.e(TAG, "ClassLoader is null when accessing system property. key=" + key + ", def=" + def);
      return def;
    }
    if (key == null || key.length() == 0) {
      Log.e(TAG, "Key is null or empty when accessing system property. def=" + def);
      return def;
    }

    try {
      final Class<?> systemPropertiesClass = classLoader.loadClass("android.os.SystemProperties");
      final Class<?>[] paramTypes = new Class<?>[] { String.class, boolean.class };
      final Method getBooleanMethod = systemPropertiesClass.getMethod("getBoolean", paramTypes);
      final Object[] params = new Object[] { key, def };

      final Object result = getBooleanMethod.invoke(null, params);
      if (result instanceof Boolean) {
        return (Boolean) result;
      }
      return def;
    } catch (IllegalArgumentException iAE) {
      // Preserve documented behavior (e.g., key length > 32)
      throw iAE;
    } catch (Exception e) {
      Log.e(TAG, "Failed to read boolean system property. key=" + key + ", def=" + def, e);
      return def;
    }
  }