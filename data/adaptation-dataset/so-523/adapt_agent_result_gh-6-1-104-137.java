public static boolean isLocationEnabledForScanning_byOsServices(final Context context)
{
	// Only enforce OS-level location settings on Marshmallow and above
	if( false == Utils.isMarshmallow() )
	{
		return true;
	}

	// Marshmallow and above
	if( Utils.isKitKat() )
	{
		try
		{
			final int locationMode = android.provider.Settings.Secure.getInt(
					context.getContentResolver(),
					android.provider.Settings.Secure.LOCATION_MODE
			);

			return locationMode != android.provider.Settings.Secure.LOCATION_MODE_OFF;
		}
		catch( android.provider.Settings.SettingNotFoundException e )
		{
			// Fail fast if the setting cannot be read
			return false;
		}
	}
	else
	{
		// Pre-KitKat fallback (effectively unreachable on Marshmallow+ but kept for completeness)
		final String locationProviders = android.provider.Settings.Secure.getString(
				context.getContentResolver(),
				android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED
		);

		return false == android.text.TextUtils.isEmpty(locationProviders);
	}
}