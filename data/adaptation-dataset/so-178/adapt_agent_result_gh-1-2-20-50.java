public boolean getLocation(Context context, LocationResult result) {
    locationResult = result;
    if (lm == null) {
        lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    try {
        gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    } catch (Exception ex) {
    }
    try {
        network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    } catch (Exception ex) {
    }

    if (!gps_enabled && !network_enabled) {
        return false;
    }

    if (gps_enabled) {
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
    }
    if (network_enabled) {
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
    }

    timer1 = new Timer();
    timer1.schedule(new GetLastLocation(), 30000);
    return true;
}