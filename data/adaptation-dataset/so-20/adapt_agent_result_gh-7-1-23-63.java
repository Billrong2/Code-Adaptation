/**
     * Returns a set of the unique names of all query parameters. Iterating
     * over the set will return the names in order of their first occurrence.
     * <p>
     * Extracted from {@link android.net.Uri#getQueryParameterNames()} for
     * compatibility with older API levels.
     *
     * @throws UnsupportedOperationException if this isn't a hierarchical URI
     * @throws NullPointerException if {@code uri} is {@code null}
     * @return an unmodifiable set of decoded parameter names
     * @see android.net.Uri#getQueryParameterNames()
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static Set<String> getQueryParameterNames(final Uri uri) {
        if (uri == null) {
            throw new NullPointerException("uri == null");
        }
        if (uri.isOpaque()) {
            throw new UnsupportedOperationException(NOT_HIERARCHICAL);
        }

        final String query = uri.getEncodedQuery();
        if (query == null) {
            return Collections.emptySet();
        }

        final Set<String> names = new LinkedHashSet<>();
        int start = 0;
        do {
            final int next = query.indexOf('&', start);
            final int end = (next == -1) ? query.length() : next;

            int separator = query.indexOf('=', start);
            if (separator > end || separator == -1) {
                separator = end;
            }

            final String name = query.substring(start, separator);
            names.add(Uri.decode(name));

            // Move start to end of name.
            start = end + 1;
        } while (start < query.length());

        return Collections.unmodifiableSet(names);
    }