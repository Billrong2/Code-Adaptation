private static int[] getMostCommonColour(final Map<Integer, Integer> colourCounts) {
	// Guard against null or empty input
	if (colourCounts == null || colourCounts.isEmpty()) {
		return null;
	}

	// Create a typed list of entries for sorting
	final List<Entry<Integer, Integer>> entries = new LinkedList<Entry<Integer, Integer>>(colourCounts.entrySet());

	// Sort by value (frequency) using a typed comparator
	Collections.sort(entries, new Comparator<Entry<Integer, Integer>>() {
		public int compare(Entry<Integer, Integer> e1, Entry<Integer, Integer> e2) {
			final Integer v1 = e1.getValue();
			final Integer v2 = e2.getValue();
			return Integer.compare(v1, v2);
		}
	});

	// Select the entry with the highest frequency
	final Entry<Integer, Integer> mostCommonEntry = entries.get(entries.size() - 1);
	if (mostCommonEntry == null) {
		return null;
	}

	// Convert the RGB key directly into an int[] and return it
	final int[] rgb = getRGBArr(mostCommonEntry.getKey());
	if (rgb == null || rgb.length < 3) {
		return null;
	}
	return rgb;
}