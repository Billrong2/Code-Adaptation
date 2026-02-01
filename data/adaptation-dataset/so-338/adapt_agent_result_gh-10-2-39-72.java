  /**
   * Get the value for the given key, returned as a boolean.
   * If the key does not exist or an error occurs, the provided default is returned.
   *
   * @param classLoader the ClassLoader to use for reflection
   * @param key the system property key to lookup
   * @param def the default value to return
   * @return the key parsed as a boolean, or def on failure
   * @throws IllegalArgumentException if the key exceeds 32 characters
   */
  public static Boolean getBoolean(final ClassLoader classLoader, final String key, final boolean def)
      throws IllegalArgumentException {
    // Early validation
    if (key == null || key.length() == 0) {
      return def;
    }
    if (classLoader == null) {
      android.util.Log.e(TAG, "ClassLoader is null when accessing system property. key=" + key + ", def=" + def);
      return def;
    }

    try {
      final Class<?> systemPropertiesClass = classLoader.loadClass("android.os.SystemProperties");
      final java.lang.reflect.Method methodGetBoolean = systemPropertiesClass.getMethod(
          "getBoolean", new Class<?>[] { String.class, boolean.class });

      final Object result = methodGetBoolean.invoke(systemPropertiesClass, key, def);
      if (result instanceof Boolean) {
        return (Boolean) result;
      }

      // Unexpected return type; fall back to default
      android.util.Log.e(TAG, "Unexpected return type from SystemProperties.getBoolean. key=" + key + ", def=" + def);
      return def;

    } catch (IllegalArgumentException iAE) {
      // Preserve passthrough behavior
      throw iAE;
    } catch (ClassNotFoundException e) {
      android.util.Log.e(TAG, "SystemProperties class not found. key=" + key + ", def=" + def, e);
    } catch (NoSuchMethodException e) {
      android.util.Log.e(TAG, "SystemProperties.getBoolean method not found. key=" + key + ", def=" + def, e);
    } catch (IllegalAccessException e) {
      android.util.Log.e(TAG, "Illegal access invoking SystemProperties.getBoolean. key=" + key + ", def=" + def, e);
    } catch (java.lang.reflect.InvocationTargetException e) {
      android.util.Log.e(TAG, "InvocationTargetException invoking SystemProperties.getBoolean. key=" + key + ", def=" + def, e);
    } catch (Exception e) {
      android.util.Log.e(TAG, "Unexpected exception accessing SystemProperties.getBoolean. key=" + key + ", def=" + def, e);
    }

    return def;
  }