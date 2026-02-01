    /**
     * Converts geographic coordinates (latitude/longitude in degrees) to UTM coordinates.
     * This is a pure function with no side effects; it computes the UTM zone, latitudinal band,
     * easting, and northing (meters), including the southern-hemisphere offset and rounds
     * easting/northing to two decimal places.
     *
     * @param loc input location containing latitude and longitude in degrees
     * @return a UTMTuple representing the corresponding UTM coordinate
     * @throws IllegalArgumentException if loc is null or latitude/longitude are out of range
     */
    public static UTMTuple deg2UTM(LocTuple loc) {
        if (loc == null) {
            throw new IllegalArgumentException("LocTuple must not be null");
        }
        double lat = loc.getLatitude();
        double lon = loc.getLongitude();
        if (lat < -90.0 || lat > 90.0) {
            throw new IllegalArgumentException("Latitude out of range: " + lat);
        }
        if (lon < -180.0 || lon > 180.0) {
            throw new IllegalArgumentException("Longitude out of range: " + lon);
        }

        // Ellipsoid / projection constants (WGS84-derived, as used in original snippet)
        final double k0 = 0.9996;
        final double a = 6399593.625;
        final double e2p = 0.006739496742;
        final double e = 0.0820944379;

        int zone = (int) Math.floor(lon / 6.0 + 31.0);

        char letter;
        if (lat < -72) letter = 'C';
        else if (lat < -64) letter = 'D';
        else if (lat < -56) letter = 'E';
        else if (lat < -48) letter = 'F';
        else if (lat < -40) letter = 'G';
        else if (lat < -32) letter = 'H';
        else if (lat < -24) letter = 'J';
        else if (lat < -16) letter = 'K';
        else if (lat < -8)  letter = 'L';
        else if (lat < 0)   letter = 'M';
        else if (lat < 8)   letter = 'N';
        else if (lat < 16)  letter = 'P';
        else if (lat < 24)  letter = 'Q';
        else if (lat < 32)  letter = 'R';
        else if (lat < 40)  letter = 'S';
        else if (lat < 48)  letter = 'T';
        else if (lat < 56)  letter = 'U';
        else if (lat < 64)  letter = 'V';
        else if (lat < 72)  letter = 'W';
        else                letter = 'X';

        double latRad = Math.toRadians(lat);
        double lonRad = Math.toRadians(lon);
        double lonOriginRad = Math.toRadians(6 * zone - 183);

        double cosLat = Math.cos(latRad);
        double sinLonDiff = Math.sin(lonRad - lonOriginRad);

        double t = 0.5 * Math.log((1 + cosLat * sinLonDiff) / (1 - cosLat * sinLonDiff));
        double v = a * k0 / Math.sqrt(1 + Math.pow(e, 2) * Math.pow(cosLat, 2));

        double easting = v * t * (1 + Math.pow(e, 2) / 2 * Math.pow(t, 2) * Math.pow(cosLat, 2) / 3) + 500000.0;
        easting = Math.round(easting * 100.0) / 100.0;

        double northing = (Math.atan(Math.tan(latRad) / Math.cos(lonRad - lonOriginRad)) - latRad) * v *
                (1 + e2p / 2 * Math.pow(t, 2) * Math.pow(cosLat, 2))
                + k0 * a * (latRad
                - 0.005054622556 * (latRad + Math.sin(2 * latRad) / 2)
                + 4.258201531e-05 * (3 * (latRad + Math.sin(2 * latRad) / 2)
                + Math.sin(2 * latRad) * Math.pow(cosLat, 2)) / 4
                - 1.674057895e-07 * (5 * (3 * (latRad + Math.sin(2 * latRad) / 2)
                + Math.sin(2 * latRad) * Math.pow(cosLat, 2)) / 4
                + Math.sin(2 * latRad) * Math.pow(cosLat, 4)) / 3);

        if (letter < 'M') {
            northing += 10000000.0;
        }
        northing = Math.round(northing * 100.0) / 100.0;

        return new UTMTuple(zone, letter, easting, northing);
    }