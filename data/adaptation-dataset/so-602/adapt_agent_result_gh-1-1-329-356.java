private static short fromFloat(final float value) {
    // Constants
    final int FLOAT_SIGN_MASK = 0x80000000;
    final int FLOAT_EXP_MASK  = 0x7f800000;
    final int FLOAT_MANT_MASK = 0x007fffff;

    final int HALF_SIGN_MASK  = 0x8000;
    final int HALF_EXP_MASK   = 0x7c00;
    final int HALF_MANT_MASK  = 0x03ff;

    final int FLOAT_EXP_BIAS = 127;
    final int HALF_EXP_BIAS  = 15;

    // Handle NaN and Infinity explicitly
    if (Float.isNaN(value)) {
      int bits = Float.floatToIntBits(value);
      int payload = (bits & FLOAT_MANT_MASK) >>> 13;
      if (payload == 0) {
        payload = 1; // ensure NaN, not Inf
      }
      return (short) (HALF_EXP_MASK | (payload & HALF_MANT_MASK));
    }
    if (Float.isInfinite(value)) {
      return (short) (((Float.floatToIntBits(value) & FLOAT_SIGN_MASK) >>> 16) | HALF_EXP_MASK);
    }

    int bits = Float.floatToIntBits(value);
    int sign = (bits & FLOAT_SIGN_MASK) >>> 16;
    int exp  = (bits & FLOAT_EXP_MASK) >>> 23;
    int mant = bits & FLOAT_MANT_MASK;

    // Zero (preserve signed zero)
    if (exp == 0 && mant == 0) {
      return (short) sign;
    }

    // Unbias exponent
    int halfExp = exp - FLOAT_EXP_BIAS + HALF_EXP_BIAS;

    // Normalized half-float
    if (halfExp > 0 && halfExp < 0x1f) {
      // Round mantissa from 23 to 10 bits (round-to-nearest-even)
      int mantRounded = mant + 0x00001000; // 1 << (23-10-1)
      if ((mantRounded & 0x00800000) != 0) {
        // Rounding overflow into exponent
        mantRounded = 0;
        halfExp += 1;
        if (halfExp >= 0x1f) {
          // Overflow to infinity
          return (short) (sign | HALF_EXP_MASK);
        }
      }
      return (short) (sign | (halfExp << 10) | ((mantRounded >>> 13) & HALF_MANT_MASK));
    }

    // Overflow: map to infinity
    if (halfExp >= 0x1f) {
      return (short) (sign | HALF_EXP_MASK);
    }

    // Subnormal or underflow
    if (halfExp <= 0) {
      // Too small for subnormal: flush to signed zero
      if (halfExp < -10) {
        return (short) sign;
      }
      // Produce subnormal half
      // Restore implicit leading 1
      int mantWithHidden = mant | 0x00800000;
      int shift = 1 - halfExp;

      // Round-to-nearest-even while shifting
      int mantRounded = mantWithHidden + (1 << (shift - 1));
      int halfMant = mantRounded >>> (shift + 13);

      return (short) (sign | (halfMant & HALF_MANT_MASK));
    }

    // Fallback (should not reach here)
    return (short) sign;
}