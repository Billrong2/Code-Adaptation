public static <K,V extends Comparable<? super V>> java.util.SortedSet<java.util.Map.Entry<K,V>> entriesSortedByValues(java.util.Map<K,V> map, final boolean ascending)
{
	if (map == null) throw new NullPointerException("map");
	
	java.util.SortedSet<java.util.Map.Entry<K,V>> sortedEntries = new java.util.TreeSet<java.util.Map.Entry<K,V>>(
		new java.util.Comparator<java.util.Map.Entry<K,V>>()
		{
			@Override
			public int compare(java.util.Map.Entry<K,V> entry1, java.util.Map.Entry<K,V> entry2)
			{
				int res = entry1.getValue().compareTo(entry2.getValue());
				if ( ! ascending) res = -res;
				// Always return non-zero for equal values to retain all entries in the TreeSet
				return res != 0 ? res : 1;
			}
		}
	);
	
	sortedEntries.addAll(map.entrySet());
	return sortedEntries;
}