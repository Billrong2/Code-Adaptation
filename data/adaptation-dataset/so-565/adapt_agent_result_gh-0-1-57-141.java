static void toGenericSignature(final StringBuilder sb, final Type type)
{
    // Reference: StackOverflow discussion on converting java.lang.reflect.Type to a JVM generic signature
    if (type instanceof GenericArrayType)
    {
        sb.append('[');
        toGenericSignature(sb, ((GenericArrayType) type).getGenericComponentType());
    }
    else if (type instanceof ParameterizedType)
    {
        final ParameterizedType pt = (ParameterizedType) type;
        sb.append('L');
        sb.append(((Class) pt.getRawType()).getName().replace('.', '/'));
        sb.append('<');
        for (Type p : pt.getActualTypeArguments())
        {
            toGenericSignature(sb, p);
        }
        sb.append(">;");
    }
    else if (type instanceof Class)
    {
        final Class clazz = (Class) type;
        if (!clazz.isPrimitive() && !clazz.isArray())
        {
            sb.append('L');
            sb.append(clazz.getName().replace('.', '/'));
            sb.append(';');
        }
        else
        {
            sb.append(clazz.getName().replace('.', '/'));
        }
    }
    else if (type instanceof WildcardType)
    {
        final WildcardType wc = (WildcardType) type;
        final Type[] lowerBounds = wc.getLowerBounds();
        final Type[] upperBounds = wc.getUpperBounds();
        final boolean hasLower = lowerBounds != null && lowerBounds.length > 0;
        final boolean hasUpper = upperBounds != null && upperBounds.length > 0;

        if (hasUpper && hasLower && Object.class.equals(lowerBounds[0]) && Object.class.equals(upperBounds[0]))
        {
            sb.append('*');
        }
        else if (hasLower)
        {
            sb.append('-');
            for (Type b : lowerBounds)
            {
                toGenericSignature(sb, b);
            }
        }
        else if (hasUpper)
        {
            if (upperBounds.length == 1 && Object.class.equals(upperBounds[0]))
            {
                sb.append('*');
            }
            else
            {
                sb.append('+');
                for (Type b : upperBounds)
                {
                    toGenericSignature(sb, b);
                }
            }
        }
        else
        {
            sb.append('*');
        }
    }
    else if (type instanceof TypeVariable)
    {
        // Alternative (not used here) would be to emit the type variable directly: "T" + name + ";".
        // Current behavior intentionally substitutes the type variable with its first bound.
        toGenericSignature(sb, ((TypeVariable) type).getBounds()[0]);
    }
    else
    {
        throw new IllegalArgumentException("Invalid type: " + type);
    }
}