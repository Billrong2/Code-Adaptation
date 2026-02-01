protected static void findReferences(String jarName, java.util.jar.JarFile jarFile)
        throws org.apache.bcel.classfile.ClassFormatException, java.io.IOException, ClassNotFoundException
    {
        if (jarName == null)
        {
            throw new IllegalArgumentException("jarName must not be null");
        }
        if (jarFile == null)
        {
            throw new IllegalArgumentException("jarFile must not be null");
        }

        final java.util.Map<String, org.apache.bcel.classfile.JavaClass> javaClasses =
            collectJavaClasses(jarName, jarFile);
        if (javaClasses == null || javaClasses.isEmpty())
        {
            return;
        }

        for (final org.apache.bcel.classfile.JavaClass javaClass : javaClasses.values())
        {
            if (javaClass == null)
            {
                continue;
            }
            System.out.println("Class " + javaClass.getClassName());
            final java.util.Map<org.apache.bcel.classfile.JavaClass, java.util.Set<org.apache.bcel.classfile.Method>> references =
                computeReferences(javaClass, javaClasses);
            if (references == null || references.isEmpty())
            {
                continue;
            }
            for (final java.util.Map.Entry<org.apache.bcel.classfile.JavaClass, java.util.Set<org.apache.bcel.classfile.Method>> entry : references.entrySet())
            {
                final org.apache.bcel.classfile.JavaClass referencedJavaClass = entry.getKey();
                final java.util.Set<org.apache.bcel.classfile.Method> methods = entry.getValue();
                if (referencedJavaClass == null || methods == null)
                {
                    continue;
                }
                System.out.println(
                    "    is referencing class " +
                    referencedJavaClass.getClassName() + " by calling");
                for (final org.apache.bcel.classfile.Method method : methods)
                {
                    if (method == null)
                    {
                        continue;
                    }
                    System.out.println(
                        "        " + method.getName() + " with arguments " +
                        java.util.Arrays.toString(method.getArgumentTypes()));
                }
            }
        }
    }