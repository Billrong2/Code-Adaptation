public static <K, V extends Comparable<? super V>> Map<K, V> sortByDescendingValue(Map<K, V> map)
	{
		if (map == null) {
			throw new NullPointerException("map must not be null");
		}
		if (map.isEmpty()) {
			return new LinkedHashMap<K, V>();
		}
		final List<Map.Entry<K, V>> entries = new LinkedList<Map.Entry<K, V>>(map.entrySet());
		Collections.sort(entries, new Comparator<Map.Entry<K, V>>()
		{
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2)
			{
				// reverse natural order to achieve descending sort by value
				return o2.getValue().compareTo(o1.getValue());
			}
		});
		final Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : entries)
		{
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}