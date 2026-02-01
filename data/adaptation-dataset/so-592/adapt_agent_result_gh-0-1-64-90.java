    /**
     * Set Activity locale to SharedPreferences setting.
     * Must be called before.
     */
    public static void fixLocale(final Context context) {
        if (context == null) {
            return;
        }

        final android.content.res.Resources resources = context.getResources();
        if (resources == null) {
            return;
        }

        final android.content.SharedPreferences prefs = android.preference.PreferenceManager
                .getDefaultSharedPreferences(context);
        if (prefs == null) {
            return;
        }

        final String languageTag = prefs.getString(de.k3b.android.androFotoFinder.Global.PREF_KEY_USER_LOCALE, "");

        // default: use system locale
        java.util.Locale targetLocale = de.k3b.android.androFotoFinder.Global.systemLocale;

        if (languageTag != null && !languageTag.trim().isEmpty()) {
            final java.util.Locale parsed = java.util.Locale.forLanguageTag(languageTag);
            if (parsed != null && parsed.getLanguage() != null && !parsed.getLanguage().isEmpty()) {
                targetLocale = parsed;
            }
        }

        if (targetLocale == null) {
            return;
        }

        // avoid unnecessary work if locale is already active
        final java.util.Locale current = java.util.Locale.getDefault();
        if (current != null && current.equals(targetLocale)) {
            return;
        }

        java.util.Locale.setDefault(targetLocale);

        try {
            final android.content.res.Configuration config = new android.content.res.Configuration(resources.getConfiguration());
            config.setLocale(targetLocale);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                context.createConfigurationContext(config);
            } else {
                resources.updateConfiguration(config, resources.getDisplayMetrics());
            }
        } catch (RuntimeException e) {
            // ignore edge-device configuration failures
        }

        // remember locale for recreation handling
        try {
            if (context instanceof LocalizedActivity) {
                ((LocalizedActivity) context).myLocale = targetLocale;
            }
        } catch (ClassCastException e) {
            // ignore invalid cast
        }
    }