@TargetApi(19)
private static String getPathForVersionAboveEqualsVersion19(Context context, Uri uri) {
	if (context == null || uri == null) {
		return null;
	}

	try {
		// DocumentProvider (API 19+)
		if (DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				String docId = DocumentsContract.getDocumentId(uri);
				if (docId == null) {
					return null;
				}
				String[] split = docId.split(":");
				if (split.length < 2) {
					return null;
				}
				String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}
				// non-primary volumes are not handled
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {
				String id = DocumentsContract.getDocumentId(uri);
				if (id == null) {
					return null;
				}
				try {
					long downloadId = Long.parseLong(id);
					Uri contentUri = ContentUris.withAppendedId(
							Uri.parse("content://downloads/public_downloads"), downloadId);
					return getDataColumn(context, contentUri, null, null);
				} catch (NumberFormatException numberFormatException) {
					return null;
				}
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				String docId = DocumentsContract.getDocumentId(uri);
				if (docId == null) {
					return null;
				}
				String[] split = docId.split(":");
				if (split.length < 2) {
					return null;
				}
				String type = split[0];

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

				String selection = "_id=?";
				String[] selectionArgs = new String[] { split[1] };

				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		}
		// Content scheme (general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {
			// Google Photos URIs return a remote address as last path segment
			if (isGooglePhotosUri(uri)) {
				return uri.getLastPathSegment();
			}
			return getDataColumn(context, uri, null, null);
		}
		// File scheme
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}
	} catch (RuntimeException runtimeException) {
		// Defensive: ContentResolver / DocumentsContract failures
		return null;
	}

	return null;
}