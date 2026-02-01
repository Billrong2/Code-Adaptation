private static String getCurrentRuntimeValue() {
        try {
            Class<?> systemProperties = Class.forName("android.os.SystemProperties");
            Method get = systemProperties.getMethod("get", String.class, String.class);
            if (get == null) {
                return null;
            }
            try {
                final String value = (String) get.invoke(null, SELECT_RUNTIME_PROPERTY, "Dalvik");
                if (LIB_DALVIK.equals(value)) {
                    return "Dalvik";
                } else if (LIB_ART.equals(value)) {
                    return "ART";
                } else if (LIB_ART_D.equals(value)) {
                    return "ART debug build";
                }
                return value;
            } catch (IllegalAccessException e) {
                return null;
            } catch (IllegalArgumentException e) {
                return null;
            } catch (InvocationTargetException e) {
                return null;
            }
        } catch (ClassNotFoundException e) {
            return null;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }