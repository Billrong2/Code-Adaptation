    /**
     * Computes the great-circle distance between two points on Earth using the Haversine formula.
     * <p>
     * <strong>All parameters are expected to be in radians.</strong>
     * See: https://en.wikipedia.org/wiki/Haversine_formula
     * </p>
     *
     * @param lat1 latitude of the first point, in radians
     * @param lon1 longitude of the first point, in radians
     * @param lat2 latitude of the second point, in radians
     * @param lon2 longitude of the second point, in radians
     * @return distance between the two points in miles
     */
    public static double computeDistanceInMiles(final double lat1, final double lon1, final double lat2, final double lon2)
    {
        // Earth radius in miles
        final double earthRadius = 3958.75;

        // Differences are already in radians
        final double dLat = lat2 - lat1;
        final double dLon = lon2 - lon1;

        final double sinHalfDLat = Math.sin(dLat * 0.5);
        final double sinHalfDLon = Math.sin(dLon * 0.5);

        final double a = (sinHalfDLat * sinHalfDLat)
                + (sinHalfDLon * sinHalfDLon * Math.cos(lat1) * Math.cos(lat2));
        final double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0 - a));

        return earthRadius * c;
    }