/**
	 * Decomposes a floating-point value into a normalized mantissa and an exponent,
	 * such that {@code value = mantissa * 2^exponent}. The mantissa is adjusted to
	 * lie in the interval [0.5, 1.0) (or the corresponding signed range), while
	 * preserving special-case behavior for zero, NaN, and infinity.
	 *
	 * @param value
	 *            the floating-point value to decompose
	 * @return an {@link ExponentAndMantissa} containing the mantissa and exponent
	 */
	public static ExponentAndMantissa frexp(double value) {
		ExponentAndMantissa ret = new ExponentAndMantissa();

		ret.exponent = 0;
		ret.mantissa = 0;

		if (value == 0.0 || value == -0.0) {
			return ret;
		}

		if (Double.isNaN(value)) {
			ret.mantissa = Double.NaN;
			ret.exponent = -1;
			return ret;
		}

		if (Double.isInfinite(value)) {
			ret.mantissa = value;
			ret.exponent = -1;
			return ret;
		}

		ret.mantissa = value;
		ret.exponent = 0;
		int sign = 1;

		if (ret.mantissa < 0f) {
			sign--;
			ret.mantissa = -(ret.mantissa);
		}
		while (ret.mantissa < 0.5f) {
			ret.mantissa *= 2.0f;
			ret.exponent -= 1;
		}
		while (ret.mantissa >= 1.0f) {
			ret.mantissa *= 0.5f;
			ret.exponent++;
		}
		ret.mantissa *= sign;
		return ret;
	}