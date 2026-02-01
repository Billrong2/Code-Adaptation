public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map, boolean doReverse) {
	List<Map.Entry<K, V>> entries = new LinkedList<Map.Entry<K, V>>(map.entrySet());

	Comparator<Map.Entry<K, V>> valueComparator = new Comparator<Map.Entry<K, V>>() {
		@Override
		public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
			return o1.getValue().compareTo(o2.getValue());
		}
	};

	Comparator<Map.Entry<K, V>> effectiveComparator = doReverse
			? Collections.reverseOrder(valueComparator)
			: valueComparator;

	Collections.sort(entries, effectiveComparator);

	Map<K, V> result = new LinkedHashMap<K, V>();
	for (Map.Entry<K, V> entry : entries) {
		result.put(entry.getKey(), entry.getValue());
	}
	return result;
}