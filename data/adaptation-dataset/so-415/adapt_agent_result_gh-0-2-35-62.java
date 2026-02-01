public static <T> Optional<T> findLastOf(final Stream<T> stream) {
		if (stream == null) {
			return Optional.empty();
		}

		Spliterator<T> split = stream.spliterator();

		if (split.hasCharacteristics(Spliterator.SIZED | Spliterator.SUBSIZED)) {
			for (;;) {
				Spliterator<T> part = split.trySplit();
				if (part == null) {
					break;
				}
				if (split.getExactSizeIfKnown() == 0) {
					split = part;
					break;
				}
			}
		}

		T last = null;
		for (Iterator<T> it = traverse(split); it.hasNext(); ) {
			last = it.next();
		}

		return Optional.ofNullable(last);
	}