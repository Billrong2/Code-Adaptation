/**
     * Sorts a map by its values.
     * <p>
     * Adapted from a Stack Overflow answer (original sorting logic preserved).
     * The returned map maintains insertion order corresponding to the sorted values.
     * </p>
     *
     * @param unsortMap the map to sort; may be {@code null}
     * @param order {@code true} for ascending order, {@code false} for descending order
     * @return a new {@link java.util.LinkedHashMap} containing the sorted entries
     */
    private static Map<String, Integer> sortByComparator(final Map<String, Integer> unsortMap, final boolean order) {
        if (unsortMap == null || unsortMap.isEmpty()) {
            return new LinkedHashMap<String, Integer>();
        }

        final List<Map.Entry<String, Integer>> list =
                new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(final Map.Entry<String, Integer> o1,
                               final Map.Entry<String, Integer> o2) {
                if (order) {
                    return o1.getValue().compareTo(o2.getValue());
                }
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        // Maintaining insertion order with the help of LinkedHashMap
        final Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (final Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }