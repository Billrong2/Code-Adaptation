@TargetApi(Build.VERSION_CODES.KITKAT)
protected static String getPathForV19AndUp(Context context, Uri contentUri) {
	// Defensive checks
	if (context == null || contentUri == null) {
		return null;
	}
	if (!DocumentsContract.isDocumentUri(context, contentUri)) {
		return null;
	}

	String documentId = DocumentsContract.getDocumentId(contentUri);
	if (documentId == null) {
		return null;
	}

	// Expecting format like "type:id"
	int colonIndex = documentId.indexOf(':');
	if (colonIndex == -1 || colonIndex + 1 >= documentId.length()) {
		return null;
	}
	String id = documentId.substring(colonIndex + 1);

	String[] columns = { MediaStore.Images.Media.DATA };
	String selection = MediaStore.Images.Media._ID + "=?";
	Cursor cursor = context.getContentResolver().query(
			MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
			columns,
			selection,
			new String[] { id },
			null);

	if (cursor == null) {
		return null;
	}

	String filePath = "";
	try {
		int columnIndex = cursor.getColumnIndex(columns[0]);
		if (columnIndex != -1 && cursor.moveToFirst()) {
			filePath = cursor.getString(columnIndex);
		}
	} finally {
		cursor.close();
	}

	return filePath;
}