    /**
     * Recursively search for the {@link android.preference.PreferenceScreen} that contains
     * a {@link android.preference.Preference} identified by the given key.
     * <p>
     * This method walks through the root adapter of the provided PreferenceScreen and, when
     * encountering nested PreferenceScreens, descends into them until the matching key is found.
     * </p>
     * <p>
     * Credit: original implementation inspired by a Stack Overflow answer
     * (see comment near {@link #openPreference(String)}).
     * </p>
     * <p>
     * FIXME: This approach relies on {@link android.preference.PreferenceScreen#getRootAdapter()} and
     * may behave unexpectedly with complex or deeply nested {@link android.preference.PreferenceGroup}
     * hierarchies on some Android versions.
     * </p>
     *
     * @param key    the preference key to look for
     * @param screen the PreferenceScreen to start searching from, or null to use the root screen
     * @return the PreferenceScreen containing the preference with the given key, or null if not found
     */
    private android.preference.PreferenceScreen findPreferenceScreenForPreference(final String key, android.preference.PreferenceScreen screen) {
        // Default to the activity root PreferenceScreen if none is provided
        if (screen == null) {
            screen = getPreferenceScreen();
        }

        if (screen == null) {
            return null;
        }

        android.preference.PreferenceScreen result = null;

        // Root adapter contains all direct children of this PreferenceScreen
        final android.widget.Adapter ada = screen.getRootAdapter();
        if (ada == null) {
            return null;
        }

        for (int i = 0; i < ada.getCount(); i++) {
            final Object item = ada.getItem(i);
            if (!(item instanceof android.preference.Preference)) {
                continue;
            }

            final android.preference.Preference pref = (android.preference.Preference) item;
            final String prefKey = pref.getKey();

            // Check whether this preference matches the requested key
            if (prefKey != null && prefKey.equals(key)) {
                return screen;
            }

            // Recurse into nested PreferenceScreens
            if (pref.getClass().equals(android.preference.PreferenceScreen.class)) {
                result = findPreferenceScreenForPreference(key, (android.preference.PreferenceScreen) pref);
                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }