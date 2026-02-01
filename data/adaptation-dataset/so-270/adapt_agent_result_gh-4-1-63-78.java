public static void setEnv(final java.util.Map<String, String> newenv) throws Exception {
    // Approach adapted from a common Stack Overflow solution that uses reflection
    // to modify the backing map of System.getenv()'s unmodifiable map.

    if (newenv == null) {
      throw new IllegalArgumentException("newenv must not be null");
    }

    try {
      // Get the current environment map first
      final java.util.Map<String, String> env = System.getenv();

      // Then locate the unmodifiable map implementation inside Collections
      final Class<?>[] classes = java.util.Collections.class.getDeclaredClasses();
      for (final Class<?> clazz : classes) {
        if ("java.util.Collections$UnmodifiableMap".equals(clazz.getName())) {
          final java.lang.reflect.Field field = clazz.getDeclaredField("m");
          field.setAccessible(true);
          @SuppressWarnings("unchecked")
          final java.util.Map<String, String> modifiableEnv = (java.util.Map<String, String>) field.get(env);
          modifiableEnv.clear();
          modifiableEnv.putAll(newenv);
          break;
        }
      }
    } catch (SecurityException se) {
      LOG.error("SecurityException while attempting to set environment variables via reflection", se);
      throw se;
    }
  }