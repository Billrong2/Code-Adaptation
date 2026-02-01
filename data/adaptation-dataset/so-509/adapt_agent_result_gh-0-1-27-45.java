public static ECPoint scalarMult(final ECPoint P, final BigInteger k, final EllipticCurve curve) {
    if (P == null || k == null || curve == null)
        throw new IllegalArgumentException("Null parameter");
    if (P.equals(ECPoint.POINT_INFINITY))
        return ECPoint.POINT_INFINITY;
    if (!(curve.getField() instanceof java.security.spec.ECFieldFp))
        throw new IllegalArgumentException("Only prime fields supported");
    final java.security.spec.ECFieldFp field = (java.security.spec.ECFieldFp) curve.getField();
    final BigInteger prime = field.getP();
    // validate that P is on the curve: y^2 = x^3 + ax + b (mod p)
    final BigInteger x = P.getAffineX();
    final BigInteger y = P.getAffineY();
    if (x == null || y == null)
        throw new IllegalArgumentException("Point has no affine coordinates");
    BigInteger lhs = y.modPow(TWO, prime);
    BigInteger rhs = x.modPow(THREE, prime).add(curve.getA().multiply(x)).add(curve.getB()).mod(prime);
    if (!lhs.equals(rhs))
        throw new IllegalArgumentException("Point is not on the given curve");
    // reduce scalar modulo field prime as required
    BigInteger scalar = k.mod(prime);
    if (scalar.signum() == 0)
        return ECPoint.POINT_INFINITY;
    if (scalar.equals(BigInteger.ONE))
        return P;
    int length = scalar.bitLength();
    byte[] bits = new byte[length];
    BigInteger tmpK = scalar;
    for (int i = 0; i <= length - 1; i++) {
        bits[i] = tmpK.mod(TWO).byteValue();
        tmpK = tmpK.divide(TWO);
    }
    ECPoint R = ECPoint.POINT_INFINITY;
    for (int i = length - 1; i >= 0; i--) {
        R = doublePoint(R, curve);
        if (bits[i] == 1)
            R = addPoint(R, P, curve);
    }
    return R;
}