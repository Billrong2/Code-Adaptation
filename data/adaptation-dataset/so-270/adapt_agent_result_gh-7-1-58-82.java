/**
   * Remove an environment variable from inside the JVM.
   * <p>
   * This method attempts to mutate the underlying environment map via reflection
   * and removes the entry associated with the given key. If the key is null or empty,
   * or if the environment cannot be modified due to JVM restrictions, the call is a no-op.
   * </p>
   *
   * @param key the environment variable identifier to remove
   */
  @SuppressWarnings("unchecked")
  public void unset(String key) {
    if (key == null || key.length() == 0) {
      return;
    }

    try {
      final Class<?>[] classes = java.util.Collections.class.getDeclaredClasses();
      final java.util.Map<String, String> env = System.getenv();
      for (final Class<?> cl : classes) {
        if ("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
          final java.lang.reflect.Field field = cl.getDeclaredField("m");
          field.setAccessible(true);
          final Object obj = field.get(env);
          final java.util.Map<String, String> map = (java.util.Map<String, String>) obj;
          map.remove(key);
        }
      }
    } catch (IllegalAccessException e) {
      // nothing we can do
    } catch (NoSuchFieldException e) {
      // nothing we can do
    }
  }