public static final <T> List<List<T>> split(final List<T> original, final int maxListSize, final Class<? extends List> listImplementation)
{
	if (original == null)
	{
		throw new NullPointerException("original must not be null");
	}
	if (listImplementation == null)
	{
		throw new NullPointerException("listImplementation must not be null");
	}
	if (maxListSize <= 0)
	{
		throw new IllegalArgumentException("maxListSize must be greater than zero");
	}

	@SuppressWarnings("unchecked")
	final T[] elements = (T[]) original.toArray();
	final int maxChunks = (int) Math.ceil(elements.length / (double) maxListSize);

	final List<List<T>> lists = new ArrayList<List<T>>(maxChunks);
	for (int i = 0; i < maxChunks; i++)
	{
		final int from = i * maxListSize;
		final int to = Math.min(from + maxListSize, elements.length);
		final T[] range = Arrays.copyOfRange(elements, from, to);

		lists.add(createSublist(range, listImplementation));
	}

	return lists;
}