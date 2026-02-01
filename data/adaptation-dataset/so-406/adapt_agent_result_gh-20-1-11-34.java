/**
	 * Sorts the entries of the given map by their values in <strong>descending</strong> order.
	 * <p>
	 * The returned list is mutable, but changes will not be reflected in the original map.
	 * This method is not thread-safe with respect to concurrent modifications of the input map.
	 * </p>
	 *
	 * @param map the map whose entries should be sorted by value
	 * @param <K> the key type
	 * @param <V> the value type; must be {@link Comparable}
	 * @return a list of map entries sorted by value in descending order; an empty list if the map is {@code null}
	 * @deprecated Prefer more explicit or library-based sorting utilities; this method has known limitations
	 *             when values are {@code null} or when custom ordering is required.
	 */
	@Deprecated
	public static <K, V extends Comparable<? super V>> List<Map.Entry<K, V>> sortByValue(final Map<K, V> map) {
		if (map == null) {
			return Collections.emptyList();
		}

		final List<Map.Entry<K, V>> entries = new LinkedList<Map.Entry<K, V>>(map.entrySet());
		Collections.sort(entries, new Comparator<Map.Entry<K, V>>() {
			@Override
			public int compare(final Map.Entry<K, V> o1, final Map.Entry<K, V> o2) {
				// Descending order by value
				return o2.getValue().compareTo(o1.getValue());
			}
		});

		return entries;
	}