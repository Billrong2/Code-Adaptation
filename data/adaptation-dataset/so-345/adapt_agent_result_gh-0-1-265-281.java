public boolean contains(final int x, final int y) {
	// Ray-casting (odd-even rule) point-in-polygon test.
	// See: https://wrf.ecse.rpi.edu/Research/Short_Notes/pnpoly.html
	if (points == null || vertices < 3 || points.length < vertices * 2) {
		return false;
	}

	boolean inside = false;
	for (int i = 0, j = vertices - 1; i < vertices; j = i++) {
		final int xi = points[i * 2];
		final int yi = points[i * 2 + 1];
		final int xj = points[j * 2];
		final int yj = points[j * 2 + 1];

		// Check if the horizontal ray crosses the edge (i,j)
		final boolean intersectsY = (yi > y) != (yj > y);
		if (intersectsY) {
			final int dy = yj - yi;
			if (dy == 0) {
				// Guard against division by zero for horizontal edges
				continue;
			}
			// Compute x-coordinate of intersection; use long to reduce overflow risk
			final long intersectX = (long) (xj - xi) * (long) (y - yi) / (long) dy + xi;
			if (x < intersectX) {
				inside = !inside;
			}
		}
	}
	return inside;
}