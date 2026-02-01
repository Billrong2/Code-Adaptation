public Intent createEmailOnlyChooserIntent(Intent source, CharSequence chooserTitle) {
        // Adapted from a Stack Overflow answer: create an email-only chooser intent
        if (source == null) {
            return null;
        }

        final CharSequence safeTitle = (chooserTitle != null) ? chooserTitle : "";
        final java.util.ArrayList<Intent> emailIntents = new java.util.ArrayList<Intent>();

        android.content.pm.PackageManager pm = null;
        if (getActivity() != null) {
            pm = getActivity().getPackageManager();
        }

        if (pm != null) {
            try {
                Intent probe = new Intent(Intent.ACTION_SENDTO, android.net.Uri.fromParts("mailto", "", null));
                java.util.List<android.content.pm.ResolveInfo> activities = pm.queryIntentActivities(probe, 0);

                if (activities != null) {
                    for (android.content.pm.ResolveInfo ri : activities) {
                        if (ri == null || ri.activityInfo == null || ri.activityInfo.packageName == null || ri.activityInfo.packageName.length() == 0) {
                            continue;
                        }
                        Intent target = new Intent(source);
                        target.setPackage(ri.activityInfo.packageName);
                        emailIntents.add(target);
                    }
                }
            } catch (SecurityException se) {
                // ignore and fallback to default chooser
            } catch (RuntimeException re) {
                // ignore and fallback to default chooser
            }
        }

        if (!emailIntents.isEmpty()) {
            Intent chooserIntent = Intent.createChooser(emailIntents.remove(0), safeTitle);
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                    emailIntents.toArray(new android.os.Parcelable[emailIntents.size()]));
            return chooserIntent;
        }

        return Intent.createChooser(source, safeTitle);
    }