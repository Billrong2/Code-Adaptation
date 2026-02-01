private void prepCamera() {
	String storageState = android.os.Environment.getExternalStorageState();
	if (android.os.Environment.MEDIA_MOUNTED.equals(storageState)) {
		String basePath = null;
		try {
			basePath = Calculator.getDefaultFilePath();
		} catch (Exception e) {
			android.util.Log.e(TAG, "Failed to resolve default file path.", e);
		}
		if (basePath == null) {
			new android.app.AlertDialog.Builder(PindActivity.this)
				.setMessage("Unable to resolve storage path for camera image.")
				.setCancelable(true)
				.create().show();
			return;
		}

		String fileName = System.currentTimeMillis() + ".jpg";
		String fullPath = basePath + fileName;
		_photoFile = new java.io.File(fullPath);
		try {
			if (!_photoFile.exists()) {
				java.io.File parent = _photoFile.getParentFile();
				if (parent != null && !parent.exists()) {
					if (!parent.mkdirs()) {
						android.util.Log.e(TAG, "Could not create parent directories for " + parent.getAbsolutePath());
						return;
					}
				}
				_photoFile.createNewFile();
			}
		} catch (java.io.IOException e) {
			android.util.Log.e(TAG, "Could not create file.", e);
			return;
		}
		android.util.Log.i(TAG, fullPath);

		_fileUri = android.net.Uri.fromFile(_photoFile);
		android.content.Intent intent = new android.content.Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, _fileUri);
		startActivityForResult(intent, CAMERA_PIC_REQUEST);
	} else {
		new android.app.AlertDialog.Builder(PindActivity.this)
			.setMessage("External Storeage (SD Card) is required.\n\nCurrent state: " + storageState)
			.setCancelable(true)
			.create().show();
	}
}