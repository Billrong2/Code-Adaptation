private static PendingIntent createPendingIntent(final Context context) {
	if (context == null) {
		return null;
	}

	final PackageManager packageManager = context.getPackageManager();
	if (packageManager == null) {
		return null;
	}

	final Intent clockIntent = new Intent(Intent.ACTION_MAIN);
	clockIntent.addCategory(Intent.CATEGORY_LAUNCHER);

	// Reduced and corrected list of known clock implementations (package, class)
	final String[][] clockComponents = new String[][]{
		{"com.android.deskclock", "com.android.deskclock.DeskClock"},
		{"com.google.android.deskclock", "com.android.deskclock.DeskClock"},
		{"com.sec.android.app.clockpackage", "com.sec.android.app.clockpackage.ClockPackage"},
		{"com.asus.deskclock", "com.asus.deskclock.DeskClock"},
		{"com.htc.android.worldclock", "com.htc.android.worldclock.WorldClockTabControl"}
	};

	ComponentName resolvedComponent = null;
	for (int i = 0; i < clockComponents.length && resolvedComponent == null; i++) {
		final String packageName = clockComponents[i][0];
		final String className = clockComponents[i][1];
		try {
			final ComponentName componentName = new ComponentName(packageName, className);
			packageManager.getActivityInfo(componentName, PackageManager.GET_META_DATA);
			resolvedComponent = componentName;
		} catch (NameNotFoundException e) {
			// ignore and try next implementation
		}
	}

	if (resolvedComponent == null) {
		return null;
	}

	clockIntent.setComponent(resolvedComponent);

	final int flags;
	if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
		flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
	} else {
		flags = PendingIntent.FLAG_UPDATE_CURRENT;
	}

	return PendingIntent.getActivity(context, 0, clockIntent, flags);
}