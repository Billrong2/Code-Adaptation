	/**
	 * Returns the next permutation of GeoElements or {@code null} if all
	 * permutations have been generated. The returned array represents the
	 * current permutation state; the internal state is then advanced to the
	 * next permutation. A {@code null} return value signals termination.
	 */
	public GeoElement[] next() {
		if (arr == null) {
			return null;
		}

		// Defensive consistency check to avoid out-of-bounds access
		if (permSwappings == null || permSwappings.length > arr.length) {
			arr = null;
			return null;
		}

		final GeoElement[] res = new GeoElement[permSwappings.length];
		System.arraycopy(arr, 0, res, 0, permSwappings.length);

		// Prepare next permutation
		int i = permSwappings.length - 1;
		while (i >= 0 && permSwappings[i] == arr.length - 1) {
			// Undo the swap represented by permSwappings[i]
			swap(i, permSwappings[i]);
			permSwappings[i] = i;
			i--;
		}

		if (i < 0) {
			// No more permutations
			arr = null;
		} else {
			final int prev = permSwappings[i];
			swap(i, prev);
			final int next = prev + 1;
			permSwappings[i] = next;
			// Prepare state for the next call
			swap(i, next);
		}

		return res;
	}