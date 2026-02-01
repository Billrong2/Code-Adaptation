public static void handleStoragePermissionResult(final int requestCode, final String[] permissions, final int[] grantResults, final Context parentActivity) {
    // Utility-style callback; must be invoked manually by the caller
    if (parentActivity == null) {
        return;
    }

    if (permissions == null || grantResults == null || grantResults.length == 0) {
        return;
    }

    final String deniedMessage = "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission";

    switch (requestCode) {
        case REQUEST_WRITE_STORAGE: {
            if (grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                // Permission granted: caller may proceed with storage-dependent features
                hasPermission = true;
            } else {
                android.widget.Toast.makeText(parentActivity, deniedMessage, android.widget.Toast.LENGTH_LONG).show();
                hasPermission = false;
            }
            break;
        }
        default:
            // no-op for other request codes
            break;
    }
}