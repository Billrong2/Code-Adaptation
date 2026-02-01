static int fromFloat(float value) {
    // Convert 32-bit float to IEEE-754 half-precision stored in lower 16 bits of int
    final int HALF_SIGN_MASK = 0x8000;
    final int HALF_EXP_MASK = 0x7C00;
    final int HALF_MANT_MASK = 0x03FF;
    final int HALF_EXP_BIAS = 15;
    final int FLOAT_EXP_BIAS = 127;
    final int HALF_MAX_EXP = 0x1F;
    final int HALF_MAX_FINITE = 0x7BFF;

    int fbits = Float.floatToRawIntBits(value);
    int sign = (fbits >>> 16) & HALF_SIGN_MASK; // isolate sign once
    int fexp = (fbits >>> 23) & 0xFF;
    int fmant = fbits & 0x7FFFFF;

    // Handle NaN / Inf
    if (fexp == 0xFF) {
        if (fmant != 0) {
            // NaN: propagate payload into half mantissa (truncate)
            int payload = (fmant >>> 13) & HALF_MANT_MASK;
            if (payload == 0) {
                payload = 1; // ensure NaN mantissa is non-zero
            }
            return sign | HALF_EXP_MASK | payload;
        }
        // Infinity
        return sign | HALF_EXP_MASK;
    }

    // Handle zero (both +0 and -0)
    if (fexp == 0 && fmant == 0) {
        return sign;
    }

    // Unbias float exponent
    int exp = fexp - FLOAT_EXP_BIAS;

    // Normalized half range
    if (exp >= -14 && exp <= 15) {
        // Add implicit leading 1 for normalized float
        int mant = fmant | 0x00800000;
        // Shift mantissa from 23 to 10 bits with round-to-nearest-even
        int shift = 23 - 10;
        int mantRounded = mant >>> shift;
        int remainder = mant & ((1 << shift) - 1);
        int halfway = 1 << (shift - 1);
        if (remainder > halfway || (remainder == halfway && (mantRounded & 1) != 0)) {
            mantRounded++;
            if (mantRounded == (1 << 11)) { // mantissa overflow (11 bits incl implicit)
                mantRounded >>>= 1;
                exp++;
                if (exp > 15) {
                    // Overflow after rounding: clamp to max finite half
                    return sign | HALF_MAX_FINITE;
                }
            }
        }
        int halfExp = (exp + HALF_EXP_BIAS) << 10;
        int halfMant = mantRounded & HALF_MANT_MASK;
        return sign | halfExp | halfMant;
    }

    // Subnormal half range
    if (exp >= -24 && exp < -14) {
        // Build mantissa with implicit leading 1
        int mant = fmant | 0x00800000;
        int shift = (-exp - 14) + (23 - 10);
        if (shift > 31) {
            return sign; // underflow to zero
        }
        int mantRounded = mant >>> shift;
        int remainder = mant & ((1 << shift) - 1);
        int halfway = 1 << (shift - 1);
        if (remainder > halfway || (remainder == halfway && (mantRounded & 1) != 0)) {
            mantRounded++;
        }
        return sign | (mantRounded & HALF_MANT_MASK);
    }

    // Overflow: too large for half
    if (exp > 15) {
        return sign | HALF_EXP_MASK;
    }

    // Underflow: too small even for subnormal
    return sign;
}