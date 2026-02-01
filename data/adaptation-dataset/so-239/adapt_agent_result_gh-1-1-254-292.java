private void takeScreenshot() {
    // Source: adapted from https://stackoverflow.com/questions/2661536/how-to-programmatically-take-a-screenshot-in-android#5651242
    final String FILE_PREFIX = "visor-android.app_";
    final int JPEG_QUALITY = 90;

    // Ensure external storage is available for writing
    if (!android.os.Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
        Toast.makeText(this, "External storage not available", Toast.LENGTH_SHORT).show();
        return;
    }

    // Capture bitmap directly from the visor view (avoid deprecated drawing cache)
    Bitmap bitmap = (mVisorView != null) ? mVisorView.getBitmap() : null;
    if (bitmap == null) {
        Toast.makeText(this, "Unable to capture image", Toast.LENGTH_SHORT).show();
        return;
    }

    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", java.util.Locale.US);
    String timestamp = sdf.format(new Date());
    String outputPath = Environment.getExternalStorageDirectory().toString()
            + "/" + FILE_PREFIX + timestamp + ".jpg";

    File imageFile = new File(outputPath);

    try (FileOutputStream outputStream = new FileOutputStream(imageFile)) {
        bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, outputStream);
        outputStream.flush();

        // Inform the user where the file was saved
        Toast.makeText(this, "Saved screenshot to: " + outputPath, Toast.LENGTH_LONG).show();

        openScreenshot(imageFile);
    } catch (java.io.IOException | SecurityException e) {
        Log.e(TAG, "Failed to save screenshot", e);
        Toast.makeText(this, "Failed to save screenshot", Toast.LENGTH_SHORT).show();
    }
}