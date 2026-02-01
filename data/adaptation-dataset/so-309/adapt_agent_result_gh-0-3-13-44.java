/**
 * Verifies that a utility class is well defined.
 * <p>
 * Attribution: adapted from a Stack Overflow answer discussing validation of utility classes.
 * </p>
 *
 * @param classObject
 *            utility class to verify.
 * @throws NoSuchMethodException
 *             if the expected no-args constructor is not found
 * @throws InvocationTargetException
 *             if the constructor invocation fails
 * @throws InstantiationException
 *             if the class cannot be instantiated via reflection
 * @throws IllegalAccessException
 *             if access to the constructor is denied
 */
public static void assertUtilityClassWellDefined(final Class<?> classObject)
        throws NoSuchMethodException, InvocationTargetException,
        InstantiationException, IllegalAccessException {
    assertTrue("class must be final",
            Modifier.isFinal(classObject.getModifiers()));

    assertEquals("There must be only one constructor", 1,
            classObject.getDeclaredConstructors().length);

    final Constructor<?> constructor = classObject.getDeclaredConstructor();
    if (constructor.isAccessible()
            || !Modifier.isPrivate(constructor.getModifiers())) {
        fail("constructor is not private");
    }

    constructor.setAccessible(true);
    constructor.newInstance();
    constructor.setAccessible(false);

    for (final Method method : classObject.getMethods()) {
        if (!Modifier.isStatic(method.getModifiers())
                && method.getDeclaringClass().equals(classObject)) {
            fail("there exists a non-static method:" + method);
        }
    }
}