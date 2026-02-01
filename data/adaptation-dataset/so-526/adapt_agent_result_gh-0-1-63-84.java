protected static Map<String, JavaClass> collectJavaClasses(final String jarName, final JarFile jarFile)
        throws ClassFormatException, IOException
    {
        final Map<String, JavaClass> javaClasses = new LinkedHashMap<String, JavaClass>();
        if (jarFile == null)
        {
            return javaClasses;
        }

        final Enumeration<JarEntry> entries = jarFile.entries();
        if (entries == null)
        {
            return javaClasses;
        }

        while (entries.hasMoreElements())
        {
            final JarEntry entry = entries.nextElement();
            if (entry == null || entry.isDirectory())
            {
                continue;
            }

            final String entryName = entry.getName();
            if (entryName == null || !entryName.endsWith(".class"))
            {
                continue;
            }

            final ClassParser parser = new ClassParser(jarName, entryName);
            final JavaClass javaClass = parser.parse();
            if (javaClass != null)
            {
                javaClasses.put(javaClass.getClassName(), javaClass);
            }
        }

        return javaClasses;
    }