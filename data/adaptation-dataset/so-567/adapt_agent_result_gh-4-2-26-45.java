public static <K extends Comparable<? super K>, V> Map<K, V> sortByKey(final Map<K, V> mapToSort) {
	if (mapToSort == null || mapToSort.isEmpty()) {
		return new LinkedHashMap<K, V>();
	}

	@SuppressWarnings("unchecked")
	final Map.Entry<K, V>[] entries = mapToSort.entrySet().toArray(new Map.Entry[mapToSort.size()]);

	Arrays.sort(entries, new Comparator<Map.Entry<K, V>>() {
		@Override
		public int compare(final Map.Entry<K, V> e1, final Map.Entry<K, V> e2) {
			return e1.getKey().compareTo(e2.getKey());
		}
	});

	final Map<K, V> sortedMap = new LinkedHashMap<K, V>(entries.length);
	for (Map.Entry<K, V> entry : entries) {
		sortedMap.put(entry.getKey(), entry.getValue());
	}

	return sortedMap;
}