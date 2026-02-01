public final int getSectionForPosition(int position) {
	// Guard against invalid input
	if (position < 0 || pieces == null || pieces.size() == 0) {
		return 0;
	}

	int localPosition = position;
	int sectionOffset = 0;

	for (ListAdapter piece : pieces) {
		if (piece == null) {
			continue;
		}

		int count = piece.getCount();
		if (count <= 0) {
			// Still advance section offset if this piece contributes sections
			if (piece instanceof SectionIndexer) {
				Object[] sections = ((SectionIndexer) piece).getSections();
				if (sections != null) {
					sectionOffset += sections.length;
				}
			}
			continue;
		}

		if (localPosition < count) {
			// Found the target piece
			if (piece instanceof SectionIndexer) {
				return sectionOffset + ((SectionIndexer) piece).getSectionForPosition(localPosition);
			}
			return 0;
		}

		// Move to next piece
		localPosition -= count;

		if (piece instanceof SectionIndexer) {
			Object[] sections = ((SectionIndexer) piece).getSections();
			if (sections != null) {
				sectionOffset += sections.length;
			}
		}
	}

	return 0;
}