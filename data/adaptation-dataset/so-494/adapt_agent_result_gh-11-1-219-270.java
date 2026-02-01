private static <T> List<Class<?>> getTypeArguments(Class<T> baseClass, Class<? extends T> childClass) {
        if (baseClass == null || childClass == null) {
            throw new IllegalArgumentException("baseClass and childClass must not be null");
        }

        Map<TypeVariable<?>, Type> resolvedTypes = new HashMap<>();
        Class<?> currentClass = childClass;
        Type currentType = currentClass.getGenericSuperclass();

        // Walk up the inheritance hierarchy until we reach the base class or Object
        while (currentClass != null && !baseClass.equals(currentClass)) {
            if (currentType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) currentType;
                Class<?> rawType = (Class<?>) parameterizedType.getRawType();

                TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

                for (int i = 0; i < typeParameters.length && i < actualTypeArguments.length; i++) {
                    resolvedTypes.put(typeParameters[i], actualTypeArguments[i]);
                }

                currentClass = rawType;
            } else if (currentType instanceof Class) {
                currentClass = (Class<?>) currentType;
            } else {
                // Unexpected Type implementation; fail fast for easier debugging
                throw new IllegalArgumentException("Unsupported Type while resolving generics: " + currentType);
            }

            if (currentClass == Object.class) {
                break;
            }
            currentType = currentClass.getGenericSuperclass();
        }

        if (!baseClass.equals(currentClass)) {
            throw new IllegalArgumentException("Child class does not inherit from base class: " + baseClass);
        }

        // Resolve the base class type arguments
        List<Class<?>> typeArgumentsAsClasses = new ArrayList<>();
        Type baseType = currentType;
        Type[] baseTypeArguments;

        if (baseType instanceof ParameterizedType) {
            baseTypeArguments = ((ParameterizedType) baseType).getActualTypeArguments();
        } else {
            // Raw base class: use its type parameters
            TypeVariable<?>[] typeParameters = baseClass.getTypeParameters();
            baseTypeArguments = new Type[typeParameters.length];
            System.arraycopy(typeParameters, 0, baseTypeArguments, 0, typeParameters.length);
        }

        for (Type baseTypeArgument : baseTypeArguments) {
            Type resolvedType = baseTypeArgument;
            // Substitute type variables until a concrete type is reached
            while (resolvedType instanceof TypeVariable && resolvedTypes.containsKey(resolvedType)) {
                resolvedType = resolvedTypes.get(resolvedType);
            }

            Class<?> resolvedClass = getClass(resolvedType);
            if (resolvedClass == null) {
                throw new IllegalArgumentException("Could not resolve generic type to Class: " + resolvedType);
            }
            typeArgumentsAsClasses.add(resolvedClass);
        }

        return typeArgumentsAsClasses;
    }