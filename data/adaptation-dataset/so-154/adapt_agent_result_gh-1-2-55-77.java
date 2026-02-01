/**
	 * Creates a {@link BitSet} with the given cardinality using a reservoir sampling
	 * approach, ensuring a uniform random selection of set bits.
	 */
	static BitSet randomBitSet(int size, int cardinality, Random rnd) {
		BitSet result = new BitSet(size);
		int[] chosen = new int[cardinality];
		int i;

		for (i = 0; i < cardinality; ++i) {
			chosen[i] = i;
			result.set(i);
		}

		for (; i < size; ++i) {
			int j = rnd.nextInt(i + 1);
			if (j < cardinality) {
				result.clear(chosen[j]);
				result.set(i);
				chosen[j] = i;
			}
		}

		return result;
	}