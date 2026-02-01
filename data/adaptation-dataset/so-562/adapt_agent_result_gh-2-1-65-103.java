/**
     * Provides an {@link Iterable} over the Unicode code points of the given string.
     * <p>
     * This is a small utility to iterate by Unicode code point (advancing by
     * {@link Character#charCount(int)}) rather than by UTF-16 {@code char}.
     * It may be replaced in the future with built-in JDK code point iteration
     * support when project requirements allow.
     * </p>
     *
     * @param string the source string to iterate over
     * @return an {@code Iterable} of Unicode code points
     */
    private static Iterable<Integer> codePointIterator(final String string) {
        return new Iterable<Integer>() {
            @Override
            public java.util.Iterator<Integer> iterator() {
                return new java.util.Iterator<Integer>() {
                    private int nextIndex = 0;
                    private final int length = string.length();

                    @Override
                    public boolean hasNext() {
                        return this.nextIndex < this.length;
                    }

                    @Override
                    public Integer next() {
                        final int result = string.codePointAt(this.nextIndex);
                        this.nextIndex += Character.charCount(result);
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