public DeviceUuidFactory(final Context context) {
        if (uuid == null) {
            synchronized (DeviceUuidFactory.class) {
                if (uuid == null) {
                    final SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, 0);
                    final String id = prefs.getString(PREFS_DEVICE_ID, null);
                    if (id != null) {
                        // Use the id previously computed and stored in the prefs file
                        uuid = UUID.fromString(id);
                    } else {
                        final String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
                        try {
                            if (androidId != null && !"9774d56d682e549c".equals(androidId)) {
                                uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
                            } else {
                                final TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                                final String deviceId = telephonyManager != null ? telephonyManager.getDeviceId() : null;
                                uuid = deviceId != null
                                        ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8"))
                                        : UUID.randomUUID();
                            }
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                        // Asynchronously persist the generated UUID; no immediate success guarantee
                        final SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(PREFS_DEVICE_ID, uuid.toString());
                        editor.apply();
                    }
                }
            }
        }
    }