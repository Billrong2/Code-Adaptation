private void collectAllReachableObjects(Object root, Set<Object> currentReachabilitySet, String indent) throws IllegalArgumentException, IllegalAccessException
{
	// Null guard
	if(root == null)
		return;

	// Prevent infinite recursion / cycles
	if(currentReachabilitySet.contains(root))
		return;

	// Mark as visited before traversing children
	currentReachabilitySet.add(root);

	Class<?> clazz = root.getClass();

	// Handle arrays explicitly
	if(clazz.isArray())	{
		int length = java.lang.reflect.Array.getLength(root);
		for(int i = 0; i < length; i++)
		{
			Object element = java.lang.reflect.Array.get(root, i);
			if(element != null)
				collectAllReachableObjects(element, currentReachabilitySet, indent);
		}
		return;
	}

	// Traverse fields across the class hierarchy
	for(Class<?> currentClass = clazz; currentClass != null; currentClass = currentClass.getSuperclass())	{
		java.lang.reflect.Field[] fields = currentClass.getDeclaredFields();
		for(java.lang.reflect.Field field : fields)
		{
			// Skip static fields
			if(java.lang.reflect.Modifier.isStatic(field.getModifiers()))
				continue;

			boolean wasAccessible = field.isAccessible();
			try
			{
				if(!wasAccessible)
					field.setAccessible(true);

				Object value = field.get(root);
				if(value == null)
					continue;

				// Default destination is strong reachability
				Set<Object> destinationSet = stronglyReachable;

				// Special handling for java.lang.ref.Reference subclasses
				if(root instanceof java.lang.ref.Reference && REFERENCE_REFERENT_FIELD != null && field.equals(REFERENCE_REFERENT_FIELD))
				{
					if(root instanceof java.lang.ref.SoftReference)
						destinationSet = softlyReachable;
					else if(root instanceof java.lang.ref.WeakReference)
						destinationSet = weaklyReachable;
					else if(root instanceof java.lang.ref.PhantomReference)
						destinationSet = phantomReachable;
				}

				collectAllReachableObjects(value, destinationSet, indent);
			}
			finally
			{
				// Restore original accessibility
				if(!wasAccessible)
					field.setAccessible(wasAccessible);
			}
		}
	}
}