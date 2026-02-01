/**
 * Calculates the great-circle distance between two geographic coordinates using the Haversine formula.
 * <p>
 * The returned distance is measured in meters, based on an Earth radius of 6,371,000 meters.
 * </p>
 *
 * @param lat1 latitude of the first point in decimal degrees
 * @param lng1 longitude of the first point in decimal degrees
 * @param lat2 latitude of the second point in decimal degrees
 * @param lng2 longitude of the second point in decimal degrees
 * @return the distance between the two points in meters
 * @see <a href="https://en.wikipedia.org/wiki/Haversine_formula">Haversine formula (Wikipedia)</a>
 */
private static double distFrom(final double lat1, final double lng1, final double lat2, final double lng2) {
    final double EARTH_RADIUS_METERS = 6_371_000.0;

    final double dLat = Math.toRadians(lat2 - lat1);
    final double dLng = Math.toRadians(lng2 - lng1);

    final double sinDLat = Math.sin(dLat / 2);
    final double sinDLng = Math.sin(dLng / 2);

    final double a = Math.pow(sinDLat, 2)
            + Math.pow(sinDLng, 2)
            * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

    final double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    return EARTH_RADIUS_METERS * c;
}