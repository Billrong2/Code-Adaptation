public final int getSectionForPosition(final int position) {
	if (position < 0 || pieces == null) {
		return 0;
	}

	int localPosition = position;
	int sectionOffset = 0;

	for (final ListAdapter piece : pieces) {
		if (piece == null) {
			continue;
		}

		final int count = piece.getCount();

		if (localPosition < count) {
			if (piece instanceof SectionIndexer) {
				final SectionIndexer indexer = (SectionIndexer) piece;
				final int localSection = indexer.getSectionForPosition(localPosition);
				return sectionOffset + Math.max(localSection, 0);
			}
			return 0;
		}

		localPosition -= count;

		if (piece instanceof SectionIndexer) {
			final Object[] sections = ((SectionIndexer) piece).getSections();
			if (sections != null) {
				sectionOffset += sections.length;
			}
		}
	}

	return 0;
}