public static void setEnv(String name, String value) {
    if (name == null || name.isEmpty() || value == null) {
        return;
    }

    final Map<String, String> newEnv = new HashMap<String, String>(1);
    newEnv.put(name, value);

    try {
        Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
        final Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
        theEnvironmentField.setAccessible(true);
        @SuppressWarnings("unchecked")
        final Map<String, String> env = (Map<String, String>) theEnvironmentField.get(null);
        env.putAll(newEnv);

        final Field theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
        theCaseInsensitiveEnvironmentField.setAccessible(true);
        @SuppressWarnings("unchecked")
        final Map<String, String> cienv = (Map<String, String>) theCaseInsensitiveEnvironmentField.get(null);
        cienv.putAll(newEnv);
    } catch (NoSuchFieldException e) {
        try {
            final Map<String, String> env = System.getenv();
            for (Class<?> cl : Collections.class.getDeclaredClasses()) {
                if ("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
                    final Field field = cl.getDeclaredField("m");
                    field.setAccessible(true);
                    @SuppressWarnings("unchecked")
                    final Map<String, String> map = (Map<String, String>) field.get(env);
                    map.clear();
                    map.putAll(newEnv);
                }
            }
        } catch (Exception ignored) {
            // NOOP: suppress all errors
        }
    } catch (Exception ignored) {
        // NOOP: suppress all errors
    }
}