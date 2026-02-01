public static Boolean getBoolean(final ClassLoader classLoader, final String key, final boolean def) throws IllegalArgumentException {
    if (classLoader == null) {
      Log.e(TAG, "ClassLoader is null, returning default for key=" + key + ", def=" + def);
      return Boolean.valueOf(def);
    }
    if (key == null) {
      Log.e(TAG, "Key is null, returning default def=" + def);
      return Boolean.valueOf(def);
    }

    Boolean result = Boolean.valueOf(def);

    try {
      @SuppressWarnings("rawtypes")
      final Class systemPropertiesClass = classLoader.loadClass("android.os.SystemProperties");

      @SuppressWarnings("rawtypes")
      final Class[] paramTypes = new Class[] { String.class, boolean.class };
      final Method getBooleanMethod = systemPropertiesClass.getMethod("getBoolean", paramTypes);

      final Object[] params = new Object[] { key, Boolean.valueOf(def) };
      final Object value = getBooleanMethod.invoke(systemPropertiesClass, params);

      if (value instanceof Boolean) {
        result = (Boolean) value;
      } else if (value != null) {
        // Defensive: handle unexpected return types
        result = Boolean.valueOf(def);
      }
    } catch (IllegalArgumentException iAE) {
      throw iAE;
    } catch (Exception e) {
      Log.e(TAG, "Failed to read system property boolean for key=" + key + ", def=" + def, e);
      result = Boolean.valueOf(def);
    }

    return result;
  }