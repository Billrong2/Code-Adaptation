public static float toFloat(int hbits) {
    // Note: only the lower 16 bits of hbits are used; higher 16 bits are ignored.
    int mant = hbits & 0x03ff;            // 10 bits mantissa
    int exp  = hbits & 0x7c00;            // 5 bits exponent
    if (exp == 0x7c00) {                  // NaN/Inf
        exp = 0x3fc00;                    // -> NaN/Inf
    } else if (exp != 0) {                // normalized value
        exp += 0x1c000;                   // exp - 15 + 127
        // removed nonstandard "smooth transition" special case;
        // normalized values always follow standard assembly path
    } else if (mant != 0) {               // subnormal value (exp == 0)
        exp = 0x1c400;                    // make it normal
        do {
            mant <<= 1;                   // mantissa * 2
            exp -= 0x400;                 // decrease exp by 1
        } while ((mant & 0x400) == 0);    // while not normal
        mant &= 0x3ff;                    // discard subnormal bit
    }                                     // else +/-0 -> +/-0
    return Float.intBitsToFloat(
            (hbits & 0x8000) << 16        // sign  << (31 - 15)
            | (exp | mant) << 13);        // value << (23 - 10)
}