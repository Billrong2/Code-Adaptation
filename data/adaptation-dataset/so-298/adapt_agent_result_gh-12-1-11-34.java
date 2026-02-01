/**
     * Sorts the given map entries by value in descending order and returns the sorted entry list.
     * <p>
     * @deprecated This method has known partial bug concerns and ambiguous expectations around
     *             ordering stability and null-handling; see the related discussion in the referenced issue.
     *             Prefer a well-defined sorting utility where these concerns are explicitly addressed.
     * </p>
     * @param map the input map whose entries should be sorted; if {@code null}, an empty list is returned
     * @param <K> key type
     * @param <V> value type (must be comparable)
     * @return a list of map entries sorted by value in descending order
     */
    @Deprecated
    public static <K, V extends Comparable<? super V>> List<Map.Entry<K, V>> sortByValue(final Map<K, V> map) {
        if (map == null) {
            return Collections.<Map.Entry<K, V>>emptyList();
        }

        final List<Map.Entry<K, V>> entries = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(final Map.Entry<K, V> o1, final Map.Entry<K, V> o2) {
                // descending order by value
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        return entries;
    }