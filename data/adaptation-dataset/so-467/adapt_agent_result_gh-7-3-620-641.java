/**
     * Calculates the great-circle distance between two geographic coordinates using the haversine formula.
     * <p>
     * All latitude/longitude values are expected in decimal degrees. The returned distance is expressed
     * in meters.
     * </p>
     *
     * @param lat1 the latitude of the first point in degrees (range: -90 to 90)
     * @param lon1 the longitude of the first point in degrees (range: -180 to 180)
     * @param lat2 the latitude of the second point in degrees (range: -90 to 90)
     * @param lon2 the longitude of the second point in degrees (range: -180 to 180)
     * @return the distance between the two points in meters, or {@link Double#NaN} if any input is invalid
     */
    private static double distFrom(final double lat1, final double lon1, final double lat2, final double lon2)
    {
        // validate input ranges
        if (lat1 < -90.0 || lat1 > 90.0 || lat2 < -90.0 || lat2 > 90.0
                || lon1 < -180.0 || lon1 > 180.0 || lon2 < -180.0 || lon2 > 180.0)
            return Double.NaN;

        // earth radius in meters
        final double earthRadiusMeters = 6_371_000d;

        // compute deltas in radians
        final double dLat = Math.toRadians(lat2 - lat1);
        final double dLon = Math.toRadians(lon2 - lon1);

        // convert latitudes to radians only where needed
        final double lat1Rad = Math.toRadians(lat1);
        final double lat2Rad = Math.toRadians(lat2);

        // haversine formula with intermediate variables for clarity
        final double sinHalfDLat = Math.sin(dLat / 2d);
        final double sinHalfDLon = Math.sin(dLon / 2d);

        final double a = sinHalfDLat * sinHalfDLat
                + Math.cos(lat1Rad) * Math.cos(lat2Rad) * sinHalfDLon * sinHalfDLon;
        final double c = 2d * Math.atan2(Math.sqrt(a), Math.sqrt(1d - a));

        return earthRadiusMeters * c;
    }