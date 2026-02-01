public Intent createEmailOnlyChooserIntent(Intent source, CharSequence chooserTitle) {
    // BugSense breadcrumb at method start
    BugSenseHandler.leaveBreadcrumb("createEmailOnlyChooserIntent");

    // Code hardening: basic null checks
    if (source == null) {
        return null;
    }
    if (chooserTitle == null) {
        chooserTitle = "";
    }

    android.app.Activity activity = getActivity();
    android.content.pm.PackageManager packageManager = (activity != null) ? activity.getPackageManager() : null;
    if (packageManager == null) {
        return Intent.createChooser(source, chooserTitle);
    }

    java.util.List<Intent> intents = new java.util.ArrayList<Intent>();

    try {
        Intent queryIntent = new Intent(Intent.ACTION_SENDTO, android.net.Uri.fromParts("mailto", "info@domain.com", null));
        java.util.List<ResolveInfo> activities = packageManager.queryIntentActivities(queryIntent, 0);

        if (activities != null && !activities.isEmpty()) {
            for (ResolveInfo resolveInfo : activities) {
                if (resolveInfo != null && resolveInfo.activityInfo != null) {
                    Intent target = new Intent(source);
                    target.setPackage(resolveInfo.activityInfo.packageName);
                    intents.add(target);
                }
            }
        }

        if (!intents.isEmpty()) {
            Intent chooserIntent = Intent.createChooser(intents.remove(0), chooserTitle);
            Parcelable[] extraIntents = intents.toArray(new Parcelable[intents.size()]);
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
            return chooserIntent;
        }
    } catch (RuntimeException e) {
        // Covers ActivityNotFoundException, SecurityException, and other runtime issues
        Log.e(DEBUG_TAG, "Error creating email-only chooser intent", e);
    }

    // Fallback to default chooser
    return Intent.createChooser(source, chooserTitle);
}