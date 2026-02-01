public static PointDouble getIntersectionPoint(Line lineA, Line lineB) {
	// Guard against null inputs
	if (lineA == null || lineB == null) {
		return null;
	}

	final double SEGMENT_TOLERANCE = 1.0;

	PointDouble aStart = lineA.getStart();
	PointDouble aEnd = lineA.getEnd();
	PointDouble bStart = lineB.getStart();
	PointDouble bEnd = lineB.getEnd();

	if (aStart == null || aEnd == null || bStart == null || bEnd == null) {
		return null;
	}

	double x1 = aStart.getX();
	double y1 = aStart.getY();
	double x2 = aEnd.getX();
	double y2 = aEnd.getY();

	double x3 = bStart.getX();
	double y3 = bStart.getY();
	double x4 = bEnd.getX();
	double y4 = bEnd.getY();

	double d = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
	if (d == 0) {
		// parallel (or coincident) lines
		return null;
	}

	double xi = ((x3 - x4) * (x1 * y2 - y1 * x2) - (x1 - x2) * (x3 * y4 - y3 * x4)) / d;
	double yi = ((y3 - y4) * (x1 * y2 - y1 * x2) - (y1 - y2) * (x3 * y4 - y3 * x4)) / d;

	PointDouble intersection = new PointDouble(xi, yi);

	// Post-check: ensure intersection lies on both line segments (within tolerance)
	if (lineA.getDistanceToPoint(intersection) > SEGMENT_TOLERANCE) {
		return null;
	}
	if (lineB.getDistanceToPoint(intersection) > SEGMENT_TOLERANCE) {
		return null;
	}

	return intersection;
}