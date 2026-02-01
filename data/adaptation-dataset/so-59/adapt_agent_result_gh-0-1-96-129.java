public void run() {
        // Finalize the location request after the wait period and deliver result via callback only
        android.location.Location gpsLocation = null;
        android.location.Location networkLocation = null;
        android.location.Location bestLocation = null;

        // Always attempt to unregister listeners to prevent leaks
        try {
            if (mLocationManager != null) {
                if (locationListenerGps != null) {
                    mLocationManager.removeUpdates(locationListenerGps);
                }
                if (locationListenerNetwork != null) {
                    mLocationManager.removeUpdates(locationListenerNetwork);
                }
            }
        } catch (SecurityException | IllegalStateException ignored) {
            // Ignored to keep timer thread from crashing
        }

        try {
            if (mLocationManager != null) {
                // Retrieve last known locations only if providers were enabled
                if (mGpsEnabled) {
                    try {
                        gpsLocation = mLocationManager.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER);
                    } catch (SecurityException ignored) {
                    }
                }
                if (mNetworkEnabled) {
                    try {
                        networkLocation = mLocationManager.getLastKnownLocation(android.location.LocationManager.NETWORK_PROVIDER);
                    } catch (SecurityException ignored) {
                    }
                }

                // Choose the most recent available location
                if (gpsLocation != null && networkLocation != null) {
                    bestLocation = (gpsLocation.getTime() >= networkLocation.getTime())
                            ? gpsLocation
                            : networkLocation;
                } else if (gpsLocation != null) {
                    bestLocation = gpsLocation;
                } else if (networkLocation != null) {
                    bestLocation = networkLocation;
                }
            }
        } catch (Exception ignored) {
            // Defensive: ensure no exception escapes the Timer thread
        } finally {
            // Cancel timer and deliver result (may be null)
            try {
                if (mTimer != null) {
                    mTimer.cancel();
                }
            } catch (Exception ignored) {
            }

            if (mLocationResult != null) {
                mLocationResult.gotLocation(bestLocation);
            }
        }
    }