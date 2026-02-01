/**
     * Sets environment variables for the current JVM process using reflection.
     * <p>
     * <strong>Test-only hack:</strong> This relies on JDK internals and may break across
     * Java versions or vendors. It should only be used in unit/integration tests.
     * <p>
     * Based on a well-known Stack Overflow workaround:
     * https://stackoverflow.com/questions/318239/how-do-i-set-environment-variables-from-java
     *
     * @param newEnv map of environment variables to add/override; ignored if null or empty
     */
    @SuppressWarnings("unchecked")
    public static void setEnvironmentVariables(final Map<String, String> newEnv) {
        if (newEnv == null || newEnv.isEmpty()) {
            return; // nothing to do
        }
        try {
            // This branch works for some Oracle/OpenJDK implementations
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
            // Fallback for other Java versions where ProcessEnvironment internals differ
            try {
                final Class<?>[] classes = Collections.class.getDeclaredClasses();
                final Map<String, String> env = System.getenv();
                for (final Class<?> cl : classes) {
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
                // Broad catch is intentional due to reflective access across JVM versions
                e2.printStackTrace();
            }
        } catch (Exception e1) {
            // Broad catch is intentional due to reflective access across JVM versions
            e1.printStackTrace();
        }
    }