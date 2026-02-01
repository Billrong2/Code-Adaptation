private static Iterable<Integer> codePointIterator(final String string) {
        if (string == null) {
            throw new NullPointerException("string must not be null");
        }
        class CodePointIterable implements Iterable<Integer> {
            private final String value;
            CodePointIterable(final String value) {
                this.value = value;
            }
            @Override
            public Iterator<Integer> iterator() {
                return new Iterator<Integer>() {
                    private int nextIndex = 0;
                    private final int length = CodePointIterable.this.value.length();
                    @Override
                    public boolean hasNext() {
                        return this.nextIndex < this.length;
                    }
                    @Override
                    public Integer next() {
                        final int result = CodePointIterable.this.value.codePointAt(this.nextIndex);
                        this.nextIndex += Character.charCount(result);
                        return result;
                    }
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        }
        return new CodePointIterable(string);
    }