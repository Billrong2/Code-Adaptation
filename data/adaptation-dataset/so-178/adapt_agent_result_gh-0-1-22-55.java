@SuppressLint("MissingPermission")
public boolean getLocation(Activity activity, LocationResult result) {
    mLocationResult = result;
    if (mLocationManager == null) {
        mLocationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
    }

    try {
        mGpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    } catch (Exception ignored) {
        mGpsEnabled = false;
    }
    try {
        mNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    } catch (Exception ignored) {
        mNetworkEnabled = false;
    }

    // don't start listeners if no provider is enabled
    if (!mGpsEnabled && !mNetworkEnabled) {
        return false;
    }

    if (mGpsEnabled) {
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
    }
    if (mNetworkEnabled) {
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
    }

    mTimer = new Timer();
    // fallback timeout increased from 20s to 30s
    mTimer.schedule(new GetLastLocation(), 30000);
    return true;
}