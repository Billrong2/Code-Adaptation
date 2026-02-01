public static Iterable<Integer> codePoints(final String string) {
    // Adapted from a Stack Overflow answer demonstrating safe iteration over Unicode code points.
    // TODO: Consider using Java 8+ String.codePoints() when project baseline allows it.
    return new Iterable<Integer>() {
        @Override
        public java.util.Iterator<Integer> iterator() {
            return new java.util.Iterator<Integer>() {
                private int nextIndex = 0;

                @Override
                public boolean hasNext() {
                    return nextIndex < string.length();
                }

                @Override
                public Integer next() {
                    final int result = string.codePointAt(nextIndex);
                    nextIndex += Character.charCount(result);
                    return result;
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
    };
}