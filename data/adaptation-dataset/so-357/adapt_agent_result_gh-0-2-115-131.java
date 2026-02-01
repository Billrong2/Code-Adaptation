/**
	 * Generates a random probability distribution of length {@code n} whose values are positive and sum to 1.
	 * The distribution is produced by sampling exponential-like positive values and normalizing them.
	 * A provided {@link java.util.Random} instance is used to allow reproducible results.
	 *
	 * @param n the number of probabilities to generate; if {@code n <= 0}, an empty array is returned
	 * @param random the random generator to use (must not be {@code null})
	 * @return a probability distribution array of length {@code n} summing to 1
	 */
	private static double[] getProbabilityDistribution(final int n, final Random random) {
		if (n <= 0) {
			return new double[0];
		}
		if (random == null) {
			throw new IllegalArgumentException("Random generator must not be null");
		}

		final double[] distribution = new double[n];
		double sum = 0.0d;

		for (int i = 0; i < n; i++) {
			// Generate a positive sample using inverse transform sampling
			final double u = 1.0d - random.nextDouble();
			final double value = -1.0d * Math.log(u);
			distribution[i] = value;
			sum += value;
		}

		// Guard against numerical issues; fallback to uniform distribution if sum is zero
		if (sum <= 0.0d) {
			final double uniform = 1.0d / n;
			for (int i = 0; i < n; i++) {
				distribution[i] = uniform;
			}
			return distribution;
		}

		for (int i = 0; i < n; i++) {
			distribution[i] /= sum;
		}

		return distribution;
	}