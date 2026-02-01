public static <K,V extends Comparable<? super V>> SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map, final boolean ascending)
{
	if (map == null) throw new NullPointerException("map");
	
	SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
		new Comparator<Map.Entry<K,V>>()
		{
			@Override
			public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2)
			{
				V v1 = e1.getValue();
				V v2 = e2.getValue();
				
				int res;
				if (ascending)
				{
					res = v1.compareTo(v2);
				}
				else
				{
					res = v2.compareTo(v1);
				}
				
				// Special fix to preserve items with equal values
				return res != 0 ? res : 1;
			}
		}
	);
	
	sortedEntries.addAll(map.entrySet());
	return sortedEntries;
}