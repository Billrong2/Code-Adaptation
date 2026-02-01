public static <K, V extends java.lang.Comparable<? super V>> java.util.Map<K, V> sortByValue(final java.util.Map<K, V> map, final boolean reverse) {
    if (map == null) {
        throw new java.lang.NullPointerException("Input map must not be null");
    }

    // Copy entries to a list for sorting
    final java.util.List<java.util.Map.Entry<K, V>> entries =
            new java.util.LinkedList<java.util.Map.Entry<K, V>>(map.entrySet());

    // Validate values and sort with configurable order
    java.util.Collections.sort(entries, new java.util.Comparator<java.util.Map.Entry<K, V>>() {
        @Override
        public int compare(java.util.Map.Entry<K, V> e1, java.util.Map.Entry<K, V> e2) {
            if (e1.getValue() == null || e2.getValue() == null) {
                throw new java.lang.IllegalArgumentException("Map values must not be null");
            }
            int cmp = e1.getValue().compareTo(e2.getValue());
            return reverse ? -cmp : cmp;
        }
    });

    // Preserve sorted iteration order
    final java.util.Map<K, V> result = new java.util.LinkedHashMap<K, V>(entries.size());
    for (java.util.Map.Entry<K, V> entry : entries) {
        result.put(entry.getKey(), entry.getValue());
    }
    return result;
}