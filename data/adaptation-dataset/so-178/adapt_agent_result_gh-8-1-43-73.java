public boolean getLocation(final Context context, final LocationResult result) {
	// Use LocationResult callback to pass location value back to caller
	if (context == null || result == null) {
		return false;
	}

	mLocationResult = result;

	if (mLocationManager == null) {
		mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	}

	if (mLocationManager == null) {
		return false;
	}

	// Exceptions may be thrown if a provider is not permitted
	try {
		mGpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	} catch (SecurityException ex) {
		// Provider not permitted or access denied
	}

	try {
		mNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	} catch (SecurityException ex) {
		// Provider not permitted or access denied
	}

	// Don't start listeners if no provider is enabled
	if (!mGpsEnabled && !mNetworkEnabled) {
		return false;
	}

	if (mGpsEnabled) {
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
	}
	if (mNetworkEnabled) {
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
	}

	// Guard against multiple timer instantiations
	if (mTimer != null) {
		mTimer.cancel();
	}

	mTimer = new Timer();
	mTimer.schedule(new GetLastLocation(), 20000);
	return true;
}