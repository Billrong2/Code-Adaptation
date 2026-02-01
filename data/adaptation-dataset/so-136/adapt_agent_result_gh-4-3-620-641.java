/**
 * Calculates the great-circle distance between two geographic coordinates using the haversine formula.
 * <p>
 * All latitude and longitude values are expected in decimal degrees.
 * The returned distance is expressed in meters.
 * </p>
 *
 * @param lat1 the latitude of the first point in decimal degrees
 * @param lng1 the longitude of the first point in decimal degrees
 * @param lat2 the latitude of the second point in decimal degrees
 * @param lng2 the longitude of the second point in decimal degrees
 * @return the distance between the two points on Earth in meters
 */
public static double distFrom(final double lat1, final double lng1, final double lat2, final double lng2)
{
    // mean radius of the Earth in meters
    final double radiusMeters = 6371000d;

    final double lat1Rad = Math.toRadians(lat1);
    final double lat2Rad = Math.toRadians(lat2);

    final double deltaLatRad = Math.toRadians(lat2 - lat1);
    final double deltaLngRad = Math.toRadians(lng2 - lng1);

    final double sinHalfDeltaLat = Math.sin(deltaLatRad * 0.5d);
    final double sinHalfDeltaLng = Math.sin(deltaLngRad * 0.5d);

    final double a = sinHalfDeltaLat * sinHalfDeltaLat
            + Math.cos(lat1Rad) * Math.cos(lat2Rad)
            * sinHalfDeltaLng * sinHalfDeltaLng;

    final double c = 2d * Math.atan2(Math.sqrt(a), Math.sqrt(1d - a));

    return radiusMeters * c;
}