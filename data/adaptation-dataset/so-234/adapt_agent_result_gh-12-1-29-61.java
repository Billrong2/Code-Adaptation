public static void setEnv(final java.util.Map<String, String> newenv) {
    if (newenv == null || newenv.isEmpty()) {
      return;
    }
    try {
      final Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
      final java.lang.reflect.Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
      theEnvironmentField.setAccessible(true);
      @SuppressWarnings("unchecked")
      final java.util.Map<String, String> env = (java.util.Map<String, String>) theEnvironmentField.get(null);
      env.putAll(newenv);

      final java.lang.reflect.Field theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
      theCaseInsensitiveEnvironmentField.setAccessible(true);
      @SuppressWarnings("unchecked")
      final java.util.Map<String, String> ciEnv = (java.util.Map<String, String>) theCaseInsensitiveEnvironmentField.get(null);
      ciEnv.putAll(newenv);
    } catch (NoSuchFieldException e) {
      try {
        final Class<?>[] declaredClasses = java.util.Collections.class.getDeclaredClasses();
        final java.util.Map<String, String> env = System.getenv();
        for (final Class<?> declaredClass : declaredClasses) {
          if ("java.util.Collections$UnmodifiableMap".equals(declaredClass.getName())) {
            final java.lang.reflect.Field field = declaredClass.getDeclaredField("m");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            final java.util.Map<String, String> map = (java.util.Map<String, String>) field.get(env);
            map.clear();
            map.putAll(newenv);
          }
        }
      } catch (ReflectiveOperationException e2) {
        e2.printStackTrace();
      }
    } catch (ReflectiveOperationException e1) {
      e1.printStackTrace();
    }
  }