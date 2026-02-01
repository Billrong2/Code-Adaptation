public static <T> List<List<T>> getPages(final Collection<T> c, final int pageSize) {
    // Preserve existing behavior: null collections yield an empty result
    if (c == null) {
        return Collections.emptyList();
    }

    // Copy to a list to allow indexed access; subLists are backed by this list
    final List<T> list = new ArrayList<>(c);

    // An empty collection has no pages
    if (list.isEmpty()) {
        return Collections.emptyList();
    }

    // Reset page size when non-positive or larger than the list size
    int effectivePageSize = pageSize;
    if (effectivePageSize <= 0 || effectivePageSize > list.size()) {
        effectivePageSize = list.size();
    }

    final int numPages = (int) Math.ceil((double) list.size() / (double) effectivePageSize);
    final List<List<T>> pages = new ArrayList<>(numPages);

    // Build pages; each subList is backed by the original list
    for (int pageNum = 0; pageNum < numPages; ) {
        pages.add(list.subList(pageNum * effectivePageSize,
                Math.min(++pageNum * effectivePageSize, list.size())));
    }

    return pages;
}