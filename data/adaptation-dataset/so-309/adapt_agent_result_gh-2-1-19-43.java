private static void assertUtilityClassWellDefined(Class<?> clazz) throws Exception {
    Assert.assertNotNull("Class under test must not be null", clazz);

    Assert.assertTrue("Class must be final", Modifier.isFinal(clazz.getModifiers()));

    Constructor<?>[] constructors = clazz.getDeclaredConstructors();
    Assert.assertEquals("There must be exactly one constructor", 1, constructors.length);

    Constructor<?> constructor = clazz.getDeclaredConstructor();
    Assert.assertTrue("Constructor must be private", Modifier.isPrivate(constructor.getModifiers()));

    boolean accessible = constructor.canAccess(null);
    if (!accessible) {
        constructor.setAccessible(true);
    }
    try {
        constructor.newInstance();
    } finally {
        if (!accessible) {
            constructor.setAccessible(false);
        }
    }

    for (Method method : clazz.getDeclaredMethods()) {
        if (!Modifier.isStatic(method.getModifiers())) {
            Assert.fail("Non-static method found: " + method.getName());
        }
    }
}