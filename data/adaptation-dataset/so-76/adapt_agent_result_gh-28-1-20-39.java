public static <K, V extends Comparable<? super V>> Map<K, V> sortByAscendingValue(Map<K, V> map)
{
	// Return empty map on null input to avoid NPE
	if (map == null)
	{
		return new LinkedHashMap<K, V>();
	}

	List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
	Collections.sort(list, new Comparator<Map.Entry<K, V>>()
	{
		public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2)
		{
			// assumes non-null values; natural ascending order
			return o1.getValue().compareTo(o2.getValue());
		}
	});

	Map<K, V> result = new LinkedHashMap<K, V>();
	for (Map.Entry<K, V> entry : list)
	{
		result.put(entry.getKey(), entry.getValue());
	}
	return result;
}