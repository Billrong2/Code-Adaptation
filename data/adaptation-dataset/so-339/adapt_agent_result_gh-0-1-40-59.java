public static void setOpenURIAppleEventHandler(final OpenUriAppleEventHandler urlHandler) {
    final Logger logger = Logger.getLogger(OSXAppleEventHelper.class.getName());

    // Validate handler
    if (urlHandler == null) {
        logger.log(Level.WARNING, "OpenUriAppleEventHandler is null; skipping Apple Event handler registration");
        return;
    }

    // Ensure we are running on macOS
    final String osName = System.getProperty("os.name");
    if (osName == null || !osName.toLowerCase().contains("mac")) {
        logger.log(Level.FINE, "Not running on macOS (os.name={0}); Apple Event handler not installed", osName);
        return;
    }

    try {
        final Class<?> applicationClass = Class.forName("com.apple.eawt.Application");
        final java.lang.reflect.Method getApplicationMethod = applicationClass.getDeclaredMethod("getApplication", (Class<?>[]) null);
        final Object application = getApplicationMethod.invoke(null, (Object[]) null);

        final Class<?> openURIHandlerClass = Class.forName(
                "com.apple.eawt.OpenURIHandler",
                false,
                applicationClass.getClassLoader()
        );
        final java.lang.reflect.Method setOpenURIHandlerMethod = applicationClass.getMethod("setOpenURIHandler", openURIHandlerClass);

        final OpenURIEventInvocationHandler invocationHandler = new OpenURIEventInvocationHandler(urlHandler);
        final Object openURIHandlerProxy = Proxy.newProxyInstance(
                openURIHandlerClass.getClassLoader(),
                new Class<?>[] { openURIHandlerClass },
                invocationHandler
        );

        setOpenURIHandlerMethod.invoke(application, openURIHandlerProxy);
    } catch (ClassNotFoundException e) {
        // com.apple.eawt is not available (newer macOS or non-Apple JVM)
        logger.log(Level.INFO, "Apple EAWT classes not found; OpenURI handler not installed", e);
    } catch (NoSuchMethodException | IllegalAccessException e) {
        logger.log(Level.SEVERE, "Reflection access error while installing OpenURI handler", e);
    } catch (java.lang.reflect.InvocationTargetException e) {
        logger.log(Level.SEVERE, "Error occurred while invoking Apple EAWT APIs", e.getCause());
    }
}