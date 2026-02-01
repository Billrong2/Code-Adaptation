public static <K, V extends Comparable<? super V>> Map<K, V>
	sortByDescendingValue(Map<K, V> map)
	{
		// Sort a map according to values in descending order
		List<Map.Entry<K, V>> entries = new LinkedList<Map.Entry<K, V>>(map.entrySet());
		Collections.sort(entries, new Comparator<Map.Entry<K, V>>()
		{
			public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2)
			{
				// reverse order: higher values come first
				return e2.getValue().compareTo(e1.getValue());
			}
		});

		Map<K, V> sortedByValueDesc = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : entries)
		{
			sortedByValueDesc.put(entry.getKey(), entry.getValue());
		}
		return sortedByValueDesc;
	}