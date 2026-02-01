@Override
public void run() {
	// Timeout reached: stop listening and deliver best last known location (or null)
	android.location.Location gpsLocation = null;
	android.location.Location networkLocation = null;

	// Basic null guards
	if (mLocationResult == null) {
		return;
	}

	try {
		if (mLocationManager != null) {
			// Always remove updates first to avoid leaks
			try {
				mLocationManager.removeUpdates(locationListenerGps);
				mLocationManager.removeUpdates(locationListenerNetwork);
			} catch (SecurityException se) {
				// Missing runtime permission; proceed with last known locations if possible
			}

			// Fetch last known locations based on enabled providers
			try {
				if (mGpsEnabled) {
					gpsLocation = mLocationManager.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER);
				}
			} catch (SecurityException se) {
				gpsLocation = null;
			}

			try {
				if (mNetworkEnabled) {
					networkLocation = mLocationManager.getLastKnownLocation(android.location.LocationManager.NETWORK_PROVIDER);
				}
			} catch (SecurityException se) {
				networkLocation = null;
			}
		}
	} catch (Exception e) {
		// Defensive: ensure callback is still delivered
	}

	// Choose most recent location
	android.location.Location bestLocation = null;
	if (gpsLocation != null && networkLocation != null) {
		bestLocation = (gpsLocation.getTime() >= networkLocation.getTime()) ? gpsLocation : networkLocation;
	} else if (gpsLocation != null) {
		bestLocation = gpsLocation;
	} else if (networkLocation != null) {
		bestLocation = networkLocation;
	}

	// Deliver result via callback (may be null)
	mLocationResult.gotLocation(bestLocation);
	return;
}