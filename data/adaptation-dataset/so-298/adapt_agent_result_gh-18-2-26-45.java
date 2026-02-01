@SuppressWarnings("unchecked")
public static <K extends Comparable<? super K>, V> Map<K, V> sortByKey(final Map<K, V> map) {
	final Map<K, V> result = new LinkedHashMap<K, V>();
	if (map == null || map.isEmpty()) {
		return result;
	}

	final Map.Entry<K, V>[] entries = map.entrySet().toArray(new Map.Entry[map.size()]);
	java.util.Arrays.sort(entries, new Comparator<Map.Entry<K, V>>() {
		@Override
		public int compare(final Map.Entry<K, V> o1, final Map.Entry<K, V> o2) {
			return o1.getKey().compareTo(o2.getKey());
		}
	});

	for (final Map.Entry<K, V> entry : entries) {
		result.put(entry.getKey(), entry.getValue());
	}
	return result;
}