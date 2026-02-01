public static long nthPrime(final long n) {
	if (n < 0L) {
		throw new IllegalArgumentException("n must be non-negative");
	}
	// Preserve special cases from original algorithm
	if (n < 2L) {
		return 2L;
	}
	if (n == 2L) {
		return 3L;
	}
	if (n == 3L) {
		return 5L;
	}

	// Defensive handling for logarithms
	final double dn = (double) n;
	final double logn = dn > 1.0 ? Math.log(dn) : 0.0;
	final double loglogn = dn > 2.0 ? Math.log(logn) : 0.0;

	long limit = (long) (dn * (logn + loglogn)) + 3L;
	long root = (long) Math.sqrt((double) limit);
	long count = 2L; // counting primes 2 and 3 implicitly

	// Normalize limit to 6k±1 representation
	switch ((int) (limit % 6L)) {
	case 0:
		limit = 2L * (limit / 6L) - 1L;
		break;
	case 5:
		limit = 2L * (limit / 6L) + 1L;
		break;
	default:
		limit = 2L * (limit / 6L);
	}

	// Normalize root similarly
	switch ((int) (root % 6L)) {
	case 0:
		root = 2L * (root / 6L) - 1L;
		break;
	case 5:
		root = 2L * (root / 6L) + 1L;
		break;
	default:
		root = 2L * (root / 6L);
	}

	// Sieve uses int indexing; ensure array size fits int
	if (limit > (long) Integer.MAX_VALUE * 32L) {
		throw new IllegalArgumentException("n too large for sieve representation");
	}
	final int dim = (int) ((limit + 31L) >> 5);
	final int[] sieve = new int[dim];

	// Sieve marking with micro-optimizations
	for (int i = 0; i < (int) root; ++i) {
		final int word = sieve[i >> 5];
		if ((word & (1 << (i & 31))) == 0) {
			final long li = (long) i;
			final long start;
			final long s1;
			final long s2;
			if ((i & 1) == 1) {
				start = li * (3L * li + 8L) + 4L;
				s1 = 4L * li + 5L;
				s2 = 2L * li + 3L;
			} else {
				start = li * (3L * li + 10L) + 7L;
				s1 = 2L * li + 3L;
				s2 = 4L * li + 7L;
			}
			for (long j = start; j < limit; j += s2) {
				final int idx = (int) j;
				sieve[idx >> 5] |= 1 << (idx & 31);
				j += s1;
				if (j >= limit) {
					break;
				}
				final int idx2 = (int) j;
				sieve[idx2 >> 5] |= 1 << (idx2 & 31);
			}
		}
	}

	int i = 0;
	for (; count < n; ++i) {
		count += popCount(~sieve[i]);
	}
	--i;
	int mask = ~sieve[i];
	int p;
	for (p = 31; count >= n; --p) {
		count -= (mask >> p) & 1;
	}

	// Final prime reconstruction, widened to long
	return 3L * ((long) p + ((long) i << 5)) + 7L + (long) (p & 1);
}