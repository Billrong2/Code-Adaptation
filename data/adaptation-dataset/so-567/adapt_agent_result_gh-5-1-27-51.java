/**
 * Sorts a map according to its keys in natural ascending order.
 * The returned map preserves the sorted iteration order.
 *
 * @param <K> the key type, must be comparable
 * @param <V> the value type
 * @param map the map to sort by key
 * @return a new {@link LinkedHashMap} sorted by keys, or an empty map if input is null
 */
public static <K extends Comparable<? super K>, V> Map<K, V> sortByKey(final Map<K, V> map) {
	if (map == null) {
		return new LinkedHashMap<K, V>();
	}

	final Map.Entry<K, V>[] entries = map.entrySet().toArray(new Map.Entry[map.size()]);

	Arrays.sort(entries, new Comparator<Map.Entry<K, V>>() {
		@Override
		public int compare(final Map.Entry<K, V> e1, final Map.Entry<K, V> e2) {
			return e1.getKey().compareTo(e2.getKey());
		}
	});

	final Map<K, V> sortedMap = new LinkedHashMap<K, V>(entries.length);
	for (final Map.Entry<K, V> entry : entries) {
		sortedMap.put(entry.getKey(), entry.getValue());
	}

	return sortedMap;
}