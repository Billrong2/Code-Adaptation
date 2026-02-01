private static double log2(final BigInteger val) {
    // Adapted from a Stack Overflow answer; computes base-2 logarithm via bit-length and mantissa extraction.
    if (val == null) {
      throw new IllegalArgumentException("val must not be null");
    }
    if (val.signum() <= 0) {
      throw new IllegalArgumentException("val must be positive");
    }

    // Get the minimum number of bits necessary to hold this value.
    final int n = val.bitLength();

    // Calculate the double-precision fraction of this number as if the binary point
    // was left of the most significant '1' bit. We extract the most significant
    // 53 bits (including the hidden bit) to build the mantissa.
    long mask = 1L << (DOUBLE_MANTISSA_BITS - 1); // 52
    long mantissa = 0L;
    int j = 0;
    for (int i = 1; i <= DOUBLE_MANTISSA_BITS; i++) {
      j = n - i;
      if (j < 0) {
        break;
      }
      if (val.testBit(j)) {
        mantissa |= mask;
      }
      mask >>>= 1;
    }

    // Round up if the next bit is 1.
    if (j > 0 && val.testBit(j - 1)) {
      mantissa++;
    }

    final double fraction = mantissa / (double) (1L << (DOUBLE_MANTISSA_BITS - 1));

    // log2(val) = (n - 1) + log2(fraction)
    // Use division by ln(2) for clarity instead of a magic constant.
    return (n - 1) + (Math.log(fraction) / LOG2);
  }