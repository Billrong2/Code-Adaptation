@TargetApi(19)
private static String getPathForVersionAboveEqualsVersion19(final Context context, final Uri uri) {
	// API-specific helper for KitKat (API 19)+ only. Callers must gate by SDK version.
	// Based on https://stackoverflow.com/a/20559175
	if (context == null || uri == null) {
		return null;
	}

	try {
		// DocumentProvider
		if (DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				if (docId == null) {
					return null;
				}
				final String[] split = docId.split(":");
				if (split.length < 2) {
					return null;
				}
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}
				// TODO handle non-primary volumes (intentionally unsupported)
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {
				final String id = DocumentsContract.getDocumentId(uri);
				if (id == null) {
					return null;
				}
				final long longId;
				try {
					longId = Long.valueOf(id);
				} catch (NumberFormatException numberFormatException) {
					return null;
				}
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"), longId);
				if (contentUri == null) {
					return null;
				}
				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				if (docId == null) {
					return null;
				}
				final String[] split = docId.split(":");
				if (split.length < 2) {
					return null;
				}
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}
				if (contentUri == null) {
					return null;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] { split[1] };

				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {
			// Return the remote address
			if (isGooglePhotosUri(uri)) {
				return uri.getLastPathSegment();
			}
			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}
	} catch (RuntimeException runtimeException) {
		// Graceful failure for provider or resolver issues on API 19+
		return null;
	}

	return null;
}