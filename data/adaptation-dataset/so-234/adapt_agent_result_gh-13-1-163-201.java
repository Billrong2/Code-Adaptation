/**
   * Sets environment variables for tests by mutating the JVM's internal view of the environment.
   * <p>
   * This method is intended for test use only. It affects only the current JVM process and does
   * not modify operating system–level environment variables.
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  private static void setTestEnv(final Map<String, String> newEnv) {
    try {
      final Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
      final Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
      theEnvironmentField.setAccessible(true);
      final Map<String, String> env = (Map<String, String>) theEnvironmentField.get(null);
      env.putAll(newEnv);

      final Field theCaseInsensitiveEnvironmentField =
          processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
      theCaseInsensitiveEnvironmentField.setAccessible(true);
      final Map<String, String> cienv =
          (Map<String, String>) theCaseInsensitiveEnvironmentField.get(null);
      cienv.putAll(newEnv);
    } catch (NoSuchFieldException e) {
      try {
        final Class[] classes = java.util.Collections.class.getDeclaredClasses();
        final Map<String, String> env = System.getenv();
        for (Class cl : classes) {
          if ("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
            final Field field = cl.getDeclaredField("m");
            field.setAccessible(true);
            final Object obj = field.get(env);
            final Map<String, String> map = (Map<String, String>) obj;
            map.clear();
            map.putAll(newEnv);
          }
        }
      } catch (Exception e2) {
        e2.printStackTrace();
      }
    } catch (Exception e1) {
      e1.printStackTrace();
    }
  }