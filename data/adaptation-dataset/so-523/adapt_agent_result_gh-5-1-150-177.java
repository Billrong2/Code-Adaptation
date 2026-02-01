public static boolean isLocationEnabled(Context context) {
        // Determines whether location services are enabled across API levels.
        // Assumes a non-null Context is provided by callers.

        // Default to a safe OFF state
        int locationMode = Settings.Secure.LOCATION_MODE_OFF;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(
                        context.getContentResolver(),
                        Settings.Secure.LOCATION_MODE);
            } catch (android.provider.Settings.SettingNotFoundException e) {
                // Failed to read setting - assume location is disabled
                Log.e(TAG, "Failed to get LOCATION_MODE from Settings.Secure", e);
                return false;
            }

            // Location is enabled unless explicitly set to OFF
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            // Pre-KITKAT behavior - check allowed location providers
            String locationProviders = Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }