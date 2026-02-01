public static java.util.Optional<Method> getFirstMethodAnnotatedWith(final Class<?> type, final Class<? extends Annotation> annotation) {
    if (type == null) {
        return java.util.Optional.empty();
    }

    Class<?> klass = type;
    while (klass != null && klass != Object.class) {
        final Method[] declaredMethods = klass.getDeclaredMethods();
        for (final Method method : declaredMethods) {
            // If no annotation is specified, the first encountered method is a match
            if (annotation == null || method.isAnnotationPresent(annotation)) {
                return java.util.Optional.of(method);
            }
        }
        // move to the upper class in the hierarchy
        klass = klass.getSuperclass();
    }

    return java.util.Optional.empty();
}