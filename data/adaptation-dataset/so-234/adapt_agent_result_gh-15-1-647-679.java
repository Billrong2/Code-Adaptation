private static void setEnv(Map<String, String> newenv) {
    if (newenv == null || newenv.isEmpty()) {
        return;
    }
    try {
        final Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
        final Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
        theEnvironmentField.setAccessible(true);
        @SuppressWarnings("unchecked")
        final Map<String, String> env = (Map<String, String>) theEnvironmentField.get(null);
        env.putAll(newenv);

        final Field theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
        theCaseInsensitiveEnvironmentField.setAccessible(true);
        @SuppressWarnings("unchecked")
        final Map<String, String> cienv = (Map<String, String>) theCaseInsensitiveEnvironmentField.get(null);
        cienv.putAll(newenv);
    } catch (NoSuchFieldException e) {
        try {
            final Class<?>[] classes = java.util.Collections.class.getDeclaredClasses();
            final Map<String, String> env = System.getenv();
            for (Class<?> cl : classes) {
                if ("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
                    final Field field = cl.getDeclaredField("m");
                    field.setAccessible(true);
                    @SuppressWarnings("unchecked")
                    final Map<String, String> map = (Map<String, String>) field.get(env);
                    map.clear();
                    map.putAll(newenv);
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    } catch (Exception e1) {
        e1.printStackTrace();
    }
  }