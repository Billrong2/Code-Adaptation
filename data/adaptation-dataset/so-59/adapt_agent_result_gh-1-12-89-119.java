public void run() {
        android.location.Location gpsLocation = null;
        android.location.Location networkLocation = null;
        try {
            // Always stop listening to conserve resources
            if (lm != null) {
                try {
                    lm.removeUpdates(locationListenerGps);
                } catch (Exception ignored) {
                }
                try {
                    lm.removeUpdates(locationListenerNetwork);
                } catch (Exception ignored) {
                }

                // Retrieve last known locations if providers were enabled
                try {
                    if (gps_enabled) {
                        gpsLocation = lm.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER);
                    }
                } catch (SecurityException se) {
                    android.util.Log.w("LocationHelper", "Missing permission for GPS provider", se);
                }

                try {
                    if (network_enabled) {
                        networkLocation = lm.getLastKnownLocation(android.location.LocationManager.NETWORK_PROVIDER);
                    }
                } catch (SecurityException se) {
                    android.util.Log.w("LocationHelper", "Missing permission for Network provider", se);
                }
            }

            // Choose the most recent location
            android.location.Location bestLocation = null;
            if (gpsLocation != null && networkLocation != null) {
                bestLocation = (gpsLocation.getTime() > networkLocation.getTime())
                        ? gpsLocation
                        : networkLocation;
            } else if (gpsLocation != null) {
                bestLocation = gpsLocation;
            } else if (networkLocation != null) {
                bestLocation = networkLocation;
            }

            // Deliver result via callback (may be null)
            if (locationResult != null) {
                locationResult.gotLocation(bestLocation);
            }
        } catch (Exception ex) {
            android.util.Log.e("LocationHelper", "Error while handling location timeout", ex);
            if (locationResult != null) {
                locationResult.gotLocation(null);
            }
        } finally {
            // Ensure timer is cleaned up and not reused
            if (timer1 != null) {
                try {
                    timer1.cancel();
                } catch (Exception ignored) {
                }
                timer1 = null;
            }
        }
    }