  /**
   * Convert a 16-bit half-precision floating point value to a 32-bit float.
   * <p>
   * Attribution: Adapted from a Stack Overflow answer at
   * http://stackoverflow.com/questions/6162651/half-precision-floating-point-in-java
   * </p>
   */
  private static float toFloat(final short hbits) {
    // Cast to int early to avoid sign-extension issues with bitwise operations
    final int bits = hbits & 0xFFFF;

    final int mant = bits & 0x03ff;            // 10 bits mantissa
    int exp = bits & 0x7c00;                   // 5 bits exponent
    if (exp == 0x7c00) {                       // NaN/Inf
      exp = 0x3fc00;                           // -> NaN/Inf
    } else if (exp != 0) {                     // normalized value
      exp += 0x1c000;                          // exp - 15 + 127
      if (mant == 0 && exp > 0x1c400) {         // smooth transition
        return Float.intBitsToFloat(
            ((bits & 0x8000) << 16) | (exp << 13) | 0x3ff);
      }
    } else if (mant != 0) {                    // && exp==0 -> subnormal
      exp = 0x1c400;                           // make it normal
      int m = mant;
      do {
        m <<= 1;                               // mantissa * 2
        exp -= 0x400;                          // decrease exp by 1
      } while ((m & 0x400) == 0);              // while not normal
      return Float.intBitsToFloat(
          ((bits & 0x8000) << 16) | ((exp | (m & 0x3ff)) << 13));
    }
    // +/-0 or normalized/NaN/Inf fall-through
    return Float.intBitsToFloat(
        ((bits & 0x8000) << 16) | ((exp | mant) << 13));
  }