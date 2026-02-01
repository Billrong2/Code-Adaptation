public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(final Map<K, V> map, final boolean reverse) {
        // Defensive handling of null input
        if (map == null || map.isEmpty()) {
            return new LinkedHashMap<K, V>();
        }

        final List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(final Map.Entry<K, V> o1, final Map.Entry<K, V> o2) {
                final V v1 = (o1 != null) ? o1.getValue() : null;
                final V v2 = (o2 != null) ? o2.getValue() : null;

                // Handle potential null values defensively
                int cmp;
                if (v1 == v2) {
                    cmp = 0;
                } else if (v1 == null) {
                    cmp = -1;
                } else if (v2 == null) {
                    cmp = 1;
                } else {
                    cmp = v1.compareTo(v2);
                }

                // Invert comparison for descending order when requested
                return reverse ? -cmp : cmp;
            }
        });

        final Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }