protected void parse(final WebAppContext context, final AnnotationParser parser) throws Exception
{
    if (context == null || parser == null)
        return;

    final ClassLoader webAppClassLoader = context.getClassLoader();
    final java.util.Collection<Resource> resources = new java.util.LinkedHashSet<Resource>();

    collectResources(resources, webAppClassLoader);

    for (Resource resource : resources)
    {
        if (resource == null)
            return; // early exit as requested

        try
        {
            // Clear parser state for each resource
            parser.clearHandlers();

            // Re-register all handlers for this resource
            for (DiscoverableAnnotationHandler h : _discoverableAnnotationHandlers)
            {
                if (h instanceof AbstractDiscoverableAnnotationHandler)
                    ((AbstractDiscoverableAnnotationHandler)h).reset();
                parser.registerHandler(h);
            }

            if (_classInheritanceHandler != null)
                parser.registerHandler(_classInheritanceHandler);

            for (DiscoverableAnnotationHandler h : _containerInitializerAnnotationHandlers)
                parser.registerHandler(h);

            // ClassNameResolver that excludes only system classes and respects parent-first semantics
            ClassNameResolver resolver = new ClassNameResolver()
            {
                @Override
                public boolean isExcluded(String name)
                {
                    return name != null && (name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("sun."));
                }

                @Override
                public boolean shouldOverride(String name)
                {
                    // If parent loader priority is true, do not override parent classes
                    return !context.isParentLoaderPriority();
                }
            };

            parser.parse(resource, resolver);
        }
        catch (Exception e)
        {
            // Log and continue with remaining resources, consistent with Jetty scanning behavior
            context.getServletContext().log("Annotation parsing failed for resource " + resource, e);
        }
    }
}