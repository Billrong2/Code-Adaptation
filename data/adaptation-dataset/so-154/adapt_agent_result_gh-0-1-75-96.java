/**
     * Creates a {@link BitSet} of the given size with exactly {@code cardinality} bits set to {@code true}
     * using reservoir sampling.
     *
     * <p>This implementation is based on a well-known reservoir sampling technique for selecting
     * {@code cardinality} items uniformly at random from a range {@code [0, size)} in a single pass.
     * The approach was adapted from a Stack Overflow example commonly cited for random bit set
     * generation.</p>
     */
    private static BitSet randomBitSet(int size, int cardinality, Random rnd) {
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