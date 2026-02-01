protected static String fastDoubleToString(double value, int precision) {
	/**
	 * Fast double-to-string conversion optimized for JSON serialization.
	 * <p>
	 * Precision is treated as a <em>maximum</em> fractional length: rounding is applied at the given
	 * precision, but trailing zeros are trimmed after the decimal point while ensuring at least one
	 * fractional digit remains (e.g. "x.0"). This avoids fixed-width padding and reduces output size.
	 * <p>
	 * This method avoids BigDecimal for performance reasons and is intended for finite doubles only.
	 */
	// Guard against NaN/Infinity; fall back to default string form
	if (Double.isNaN(value) || Double.isInfinite(value)) {
		return String.valueOf(value);
	}

	// Validate precision: non-negative and within POW10 bounds
	if (precision < 0) {
		precision = 0;
	} else if (precision >= POW10.length) {
		precision = POW10.length - 1;
	}

	StringBuilder sb = new StringBuilder();

	// Handle sign
	if (value < 0d) {
		sb.append('-');
		value = -value;
	}

	// Use long scale factor to avoid overflow for higher precisions
	final long scale = POW10[precision];
	final long rounded = (long) (value * scale + 0.5d);

	final long integerPart = (scale == 0L) ? rounded : (rounded / scale);
	sb.append(integerPart);

	// Fractional handling
	if (precision > 0) {
		sb.append('.');
		long fraction = (scale == 0L) ? 0L : (rounded % scale);

		if (fraction == 0L) {
			// Ensure at least one fractional digit
			sb.append('0');
		} else {
			// Zero-pad up to precision, then trim trailing zeros
			long divisor = scale / 10L;
			while (divisor > 0 && fraction < divisor) {
				sb.append('0');
				divisor /= 10L;
			}
			int start = sb.length();
			sb.append(fraction);
			// Trim trailing zeros but keep at least one fractional digit
			int end = sb.length() - 1;
			while (end > start && sb.charAt(end) == '0') {
				end--;
			}
			sb.setLength(end + 1);
		}
	}

	return sb.toString();
}