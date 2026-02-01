/**
     * Test-only utility that mutates the JVM process environment to set VOLTDB_OPTS
     * without replacing other existing environment variables. This relies on
     * JVM-specific reflection against the unmodifiable map returned by System.getenv()
     * and may not work on all Java versions.
     */
    @SuppressWarnings("unchecked")
    static void setVoltDbOpts(String voltDbOpts) throws Exception
    {
        if (voltDbOpts == null) {
            throw new IllegalArgumentException("VOLTDB_OPTS value must not be null");
        }

        final Map<String, String> env = System.getenv();
        // Create a mutable copy preserving all existing environment variables
        final Map<String, String> updated = new HashMap<String, String>(env);
        updated.put("VOLTDB_OPTS", voltDbOpts);

        try {
            Class<?> envClass = env.getClass();
            // Expect an unmodifiable map wrapper with an underlying field named "m"
            if (!"java.util.Collections$UnmodifiableMap".equals(envClass.getName())) {
                throw new IllegalStateException("Unsupported System.getenv() map type: " + envClass.getName());
            }

            Field field = envClass.getDeclaredField("m");
            try {
                field.setAccessible(true);
            } catch (SecurityException se) {
                throw new IllegalStateException("Unable to access environment map via reflection", se);
            }

            Object delegate = field.get(env);
            if (!(delegate instanceof Map)) {
                throw new IllegalStateException("Underlying environment field 'm' is not a Map");
            }

            Map<String, String> mutableEnv = (Map<String, String>) delegate;
            // Maintain all existing entries: clear then put the updated full copy
            mutableEnv.clear();
            mutableEnv.putAll(updated);
        }
        catch (Exception e) {
            // Surface clearer failures for test diagnostics
            throw new RuntimeException("Failed to set VOLTDB_OPTS in process environment", e);
        }
    }