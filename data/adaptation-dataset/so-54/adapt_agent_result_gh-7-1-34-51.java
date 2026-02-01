@Override public int compareTo(final Version that) {
        if (that == null)
            return 1;
        final String[] thisParts = this.get().split("\\.");
        final String[] thatParts = that.get().split("\\.");
        final int length = Math.max(thisParts.length, thatParts.length);
        for (final int i = 0; i < length; i++) {
            int thisPart = 0;
            int thatPart = 0;
            try {
                if (i < thisParts.length) {
                    final Integer parsed = NumberParser.parseInteger(thisParts[i]);
                    thisPart = parsed == null ? 0 : parsed.intValue();
                }
            } catch (final RuntimeException e) {
                thisPart = 0;
            }
            try {
                if (i < thatParts.length) {
                    final Integer parsed = NumberParser.parseInteger(thatParts[i]);
                    thatPart = parsed == null ? 0 : parsed.intValue();
                }
            } catch (final RuntimeException e) {
                thatPart = 0;
            }
            if (thisPart < thatPart)
                return -1;
            if (thisPart > thatPart)
                return 1;
        }
        return 0;
    }