protected static boolean copyAssetFolder(android.content.res.AssetManager assetManager, String fromAssetPath, String toPath) {
	// Reference: adapted from a StackOverflow asset-copying snippet
	if (assetManager == null || fromAssetPath == null || toPath == null) {
		return false;
	}

	boolean success = true;
	try {
		String[] assetEntries = assetManager.list(fromAssetPath);
		if (assetEntries == null) {
			return false;
		}

		java.io.File targetDir = new java.io.File(toPath);
		if (!targetDir.exists()) {
			if (!targetDir.mkdirs()) {
				// Failed to create target directory
				return false;
			}
		}

		final String separator = java.io.File.separator;
		for (String entryName : assetEntries) {
			if (entryName == null) {
				continue;
			}

			String fromPath = fromAssetPath + "/" + entryName;
			String toFilePath = toPath + separator + entryName;

			// Preserve original logic for file vs folder detection
			if (entryName.contains(".")) {
				success = success & copyAsset(assetManager, fromPath, toFilePath);
			} else {
				success = success & copyAssetFolder(assetManager, fromPath, toFilePath);
			}
		}
	} catch (java.io.IOException e) {
		android.util.Log.e("EditorActivity", "Error copying asset folder: " + fromAssetPath, e);
		return false;
	}

	return success;
}