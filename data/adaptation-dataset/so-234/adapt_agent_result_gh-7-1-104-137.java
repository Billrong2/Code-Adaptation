private void setEnv(java.util.Map<String, String> newenv) {
    // Intended for unit testing only: mutates process environment via reflection
    try {
        Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
        java.lang.reflect.Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
        theEnvironmentField.setAccessible(true);
        @SuppressWarnings("unchecked")
        java.util.Map<String, String> env = (java.util.Map<String, String>) theEnvironmentField.get(null);
        env.putAll(newenv);
        java.lang.reflect.Field theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
        theCaseInsensitiveEnvironmentField.setAccessible(true);
        @SuppressWarnings("unchecked")
        java.util.Map<String, String> cienv = (java.util.Map<String, String>) theCaseInsensitiveEnvironmentField.get(null);
        cienv.putAll(newenv);
    } catch (NoSuchFieldException e) {
        try {
            Class<?>[] classes = java.util.Collections.class.getDeclaredClasses();
            java.util.Map<String, String> env = System.getenv();
            for (Class<?> cl : classes) {
                if ("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
                    java.lang.reflect.Field field = cl.getDeclaredField("m");
                    field.setAccessible(true);
                    Object obj = field.get(env);
                    @SuppressWarnings("unchecked")
                    java.util.Map<String, String> map = (java.util.Map<String, String>) obj;
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