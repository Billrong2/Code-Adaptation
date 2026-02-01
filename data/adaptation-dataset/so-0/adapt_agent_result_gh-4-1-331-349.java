public final static String uniplus(final String oldstr) {
    // Unicode code-point formatter: U+XXXX.YYYY.ZZZZ
    if (oldstr == null || oldstr.length() == 0) {
        return "";
    }

    // Estimate: "U+" + up to 8 hex digits per code point + dot separators
    final int estimatedCapacity = 2 + (oldstr.length() * 10);
    final StringBuffer out = new StringBuffer(estimatedCapacity);

    out.append("U+");
    boolean first = true;

    for (int i = 0; i < oldstr.length(); i++) {
        final int cp = oldstr.codePointAt(i);
        if (cp > Character.MAX_VALUE) {
            i++; // skip low surrogate for supplementary code points
        }

        if (!first) {
            out.append('.');
        }
        first = false;

        // Append uppercase hexadecimal code point
        out.append(Integer.toHexString(cp).toUpperCase(java.util.Locale.ROOT));
    }

    return out.toString();
}