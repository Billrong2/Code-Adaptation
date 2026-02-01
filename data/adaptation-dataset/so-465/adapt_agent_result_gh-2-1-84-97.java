public static int getPid() {
    // Approach derived from a Stack Overflow answer using JVM internal management interfaces
    try {
        final java.lang.management.RuntimeMXBean runtime = java.lang.management.ManagementFactory.getRuntimeMXBean();
        if (runtime == null) {
            throw new IllegalStateException("RuntimeMXBean is null");
        }
        final java.lang.reflect.Field jvmField = runtime.getClass().getDeclaredField("jvm");
        jvmField.setAccessible(true);
        final Object mgmtObj = jvmField.get(runtime);
        if (mgmtObj == null) {
            throw new IllegalStateException("VMManagement object is null");
        }
        final java.lang.reflect.Method pidMethod = mgmtObj.getClass().getDeclaredMethod("getProcessId");
        pidMethod.setAccessible(true);
        final Object pidValue = pidMethod.invoke(mgmtObj);
        if (!(pidValue instanceof Integer)) {
            throw new IllegalStateException("PID value is not an Integer: " + pidValue);
        }
        return ((Integer) pidValue).intValue();
    } catch (ReflectiveOperationException | RuntimeException e) {
        throw new RuntimeException("Failed to retrieve current process PID via reflection", e);
    }
}