  @SuppressWarnings("unchecked")
  public void setEnvVar(String key, String value) {
    if (key == null) {
      return;
    }
    String safeValue = (value == null) ? "" : value;
    try {
      Class<?>[] classes = java.util.Collections.class.getDeclaredClasses();
      Map<String, String> env = System.getenv();
      if (env == null) {
        return;
      }
      for (Class<?> cl : classes) {
        if ("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
          Field field = cl.getDeclaredField("m");
          field.setAccessible(true);
          Object obj = field.get(env);
          if (obj instanceof Map) {
            Map<String, String> map = (Map<String, String>) obj;
            map.put(key, safeValue);
          }
        }
      }
    } catch (IllegalAccessException e) {
      // ignore: cannot modify environment
    } catch (NoSuchFieldException e) {
      // ignore: underlying map not accessible
    } catch (SecurityException e) {
      // ignore: access denied by security manager
    }
  }