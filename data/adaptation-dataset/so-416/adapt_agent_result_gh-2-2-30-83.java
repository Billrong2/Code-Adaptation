public DeviceUuidFactory(Context context) {
		if (uuid == null) {
			synchronized (DeviceUuidFactory.class) {
				if (uuid == null) {
					if (context == null) {
						throw new IllegalArgumentException("Context must not be null");
					}
					final SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, 0);
					final String id = prefs.getString(PREFS_DEVICE_ID, null);
					if (id != null) {
						uuid = UUID.fromString(id);
					} else {
						final String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
						try {
							if (androidId != null && !"9774d56d682e549c".equals(androidId)) {
								uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
							} else {
								final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
								final String deviceId = tm != null ? tm.getDeviceId() : null;
								uuid = deviceId != null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")) : generateDeviceUuid(context);
							}
						} catch (UnsupportedEncodingException e) {
							throw new RuntimeException(e);
						}
						prefs.edit().putString(PREFS_DEVICE_ID, uuid.toString()).commit();
					}
				}
			}
		}
	}