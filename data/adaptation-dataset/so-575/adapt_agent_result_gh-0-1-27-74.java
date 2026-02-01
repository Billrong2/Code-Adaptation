/**
     * Checks whether a specific accessibility service is enabled for the given application context.
     *
     * @param context the context used to access system settings
     * @param serviceClass the accessibility service class to check
     * @return true if the specified accessibility service is enabled, false otherwise
     */
    public static boolean isServiceEnabled(final Context context, final Class<?> serviceClass) {
        if (context == null || serviceClass == null) {
            Timber.d("Context or serviceClass is null; accessibility service cannot be resolved");
            return false;
        }

        final String targetService = context.getPackageName() + "/" + serviceClass.getName();
        int accessibilityEnabled = 0;

        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    context.getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED
            );
            Timber.d("ACCESSIBILITY_ENABLED=%d", accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Timber.e(e, "Error reading ACCESSIBILITY_ENABLED setting");
            return false;
        }

        if (accessibilityEnabled != 1) {
            Timber.d("Accessibility services are disabled");
            return false;
        }

        final String settingValue = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        );

        if (TextUtils.isEmpty(settingValue)) {
            Timber.d("No enabled accessibility services found");
            return false;
        }

        final TextUtils.SimpleStringSplitter splitter = new TextUtils.SimpleStringSplitter(':');
        splitter.setString(settingValue);

        while (splitter.hasNext()) {
            final String enabledService = splitter.next();
            if (enabledService != null && enabledService.equalsIgnoreCase(targetService)) {
                Timber.d("Accessibility service %s is enabled", targetService);
                return true;
            }
        }

        Timber.d("Accessibility service %s is not enabled", targetService);
        return false;
    }