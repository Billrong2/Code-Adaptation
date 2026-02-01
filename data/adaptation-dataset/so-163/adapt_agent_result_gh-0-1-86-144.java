public static Map<JavaClass, Set<Method>> computeReferences(final JavaClass javaClass, final Map<String, JavaClass> knownJavaClasses) throws ClassNotFoundException
{
    final Map<JavaClass, Set<Method>> references = new LinkedHashMap<JavaClass, Set<Method>>();
    if (javaClass == null || knownJavaClasses == null || knownJavaClasses.isEmpty())
    {
        return references;
    }

    final ConstantPool constantPool = javaClass.getConstantPool();
    if (constantPool == null)
    {
        return references;
    }
    final ConstantPoolGen constantPoolGen = new ConstantPoolGen(constantPool);

    for (final Method method : javaClass.getMethods())
    {
        if (method == null)
        {
            continue;
        }
        InstructionList instructionList = null;
        try
        {
            final MethodGen methodGen = new MethodGen(method, javaClass.getClassName(), constantPoolGen);
            instructionList = methodGen.getInstructionList();
        }
        catch (RuntimeException e)
        {
            // Malformed bytecode or unsupported structure; skip this method
            continue;
        }
        if (instructionList == null)
        {
            continue;
        }

        for (final InstructionHandle handle : instructionList.getInstructionHandles())
        {
            if (handle == null)
            {
                continue;
            }
            final Instruction instruction = handle.getInstruction();
            if (!(instruction instanceof InvokeInstruction))
            {
                continue;
            }

            final InvokeInstruction invokeInstruction = (InvokeInstruction) instruction;
            ReferenceType referenceType;
            try
            {
                referenceType = invokeInstruction.getReferenceType(constantPoolGen);
            }
            catch (RuntimeException e)
            {
                continue;
            }
            if (!(referenceType instanceof ObjectType))
            {
                continue;
            }

            final ObjectType objectType = (ObjectType) referenceType;
            final String referencedClassName = objectType.getClassName();
            if (referencedClassName == null)
            {
                continue;
            }

            final JavaClass referencedJavaClass = knownJavaClasses.get(referencedClassName);
            if (referencedJavaClass == null)
            {
                continue;
            }

            final String invokedMethodName = invokeInstruction.getMethodName(constantPoolGen);
            final Type[] argumentTypes = invokeInstruction.getArgumentTypes(constantPoolGen);
            if (invokedMethodName == null)
            {
                continue;
            }

            final Method referencedMethod = findMethod(referencedJavaClass, invokedMethodName, argumentTypes);
            if (referencedMethod == null)
            {
                continue;
            }

            Set<Method> methods = references.get(referencedJavaClass);
            if (methods == null)
            {
                methods = new LinkedHashSet<Method>();
                references.put(referencedJavaClass, methods);
            }
            methods.add(referencedMethod);
        }
    }

    return references;
}