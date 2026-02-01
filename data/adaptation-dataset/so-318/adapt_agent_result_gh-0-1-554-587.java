/**
	 * Converts an exact {@code double} value to a rational representation as a
	 * numerator/denominator pair.
	 * <p>
	 * The conversion is exact and reflects the IEEE&nbsp;754 binary representation
	 * of the given {@code double}. Subnormal values are handled correctly. If the
	 * value is {@code NaN} or infinite, an {@link IllegalArgumentException} is thrown.
	 * </p>
	 *
	 * @param value
	 *            the {@code double} value to convert
	 * @return a {@code BigInteger[]} of length&nbsp;2 where index&nbsp;0 is the
	 *         numerator and index&nbsp;1 is the denominator
	 * @throws IllegalArgumentException
	 *             if {@code value} is {@code NaN} or infinite
	 */
	public static BigInteger[] convertToFraction(double value) {
		final int exponent = Math.getExponent(value);
		if (exponent > Double.MAX_EXPONENT) {
			// The value is infinite or NaN.
			throw new IllegalArgumentException("Illegal parameter 'value': " + value);
		}

		final long significandMask = 0x000fffffffffffffL;
		final long implicitBit = 0x0010000000000000L;

		final long positiveSignificand;
		int adjustedExponent = exponent;

		if (adjustedExponent < Double.MIN_EXPONENT) {
			// The value is subnormal.
			adjustedExponent++;
			positiveSignificand = Double.doubleToLongBits(value) & significandMask;
		} else {
			positiveSignificand = (Double.doubleToLongBits(value) & significandMask) | implicitBit;
		}

		final BigInteger significand = BigInteger.valueOf(value < 0 ? -positiveSignificand : positiveSignificand);
		adjustedExponent -= 52; // Adjust the exponent for an integral significand.

		final BigInteger coefficient = BigInteger.ONE.shiftLeft(Math.abs(adjustedExponent));
		if (adjustedExponent >= 0) {
			return new BigInteger[] { significand.multiply(coefficient), BigInteger.ONE };
		}

		final BigInteger gcd = significand.gcd(coefficient);
		return new BigInteger[] { significand.divide(gcd), coefficient.divide(gcd) };
	}