@SuppressWarnings({"unchecked", "rawtypes"})
private void setEnv(final Map<String, String> newenv) {
    // Internal/testing-only helper: mutates process environment via reflection for tests.
    if (newenv == null) {
        return; // nothing to apply
    }
    try {
        final Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
        final java.lang.reflect.Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
        theEnvironmentField.setAccessible(true);
        final Map<String, String> env = (Map<String, String>) theEnvironmentField.get(null);
        env.putAll(newenv);

        final java.lang.reflect.Field theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
        theCaseInsensitiveEnvironmentField.setAccessible(true);
        final Map<String, String> cienv = (Map<String, String>) theCaseInsensitiveEnvironmentField.get(null);
        cienv.putAll(newenv);
    } catch (NoSuchFieldException e) {
        try {
            final Class[] classes = java.util.Collections.class.getDeclaredClasses();
            final Map<String, String> env = System.getenv();
            for (final Class cl : classes) {
                if ("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
                    final java.lang.reflect.Field field = cl.getDeclaredField("m");
                    field.setAccessible(true);
                    final Object obj = field.get(env);
                    final Map<String, String> map = (Map<String, String>) obj;
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