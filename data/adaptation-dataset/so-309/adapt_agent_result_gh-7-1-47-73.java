public static void assertUtilityClassWellDefined(Class<?> clazz)
        throws NoSuchMethodException, InvocationTargetException,
        InstantiationException, IllegalAccessException {
  /**
   * Verifies that a utility class is well defined.
   * <p>
   * A well-defined utility class is final, has exactly one private
   * no-argument constructor, and declares no non-static methods.
   * </p>
   *
   * @author Savelii Zagurskii
   * @see https://stackoverflow.com/questions/4520216/how-to-add-test-for-private-constructor
   */

  if (clazz == null) {
    throw new NullPointerException("Class under test must not be null.");
  }

  Assert.assertTrue("Class must be final.", Modifier.isFinal(clazz.getModifiers()));
  Assert.assertEquals("There must be only one constructor.", 1, clazz.getDeclaredConstructors().length);

  Constructor<?> constructor = clazz.getDeclaredConstructor();
  if (constructor.canAccess(null) || !Modifier.isPrivate(constructor.getModifiers())) {
    Assert.fail("Constructor must be private.");
  }

  boolean accessible = constructor.canAccess(null);
  try {
    if (!accessible) {
      constructor.setAccessible(true);
    }
    constructor.newInstance();
  } finally {
    if (!accessible) {
      constructor.setAccessible(false);
    }
  }

  for (Method method : clazz.getMethods()) {
    if (!Modifier.isStatic(method.getModifiers())
        && method.getDeclaringClass().equals(clazz)) {
      Assert.fail("There exists a non-static method: " + method);
    }
  }
}