@SuppressLint("NewApi")
public static String getPath(final Context context, final Uri uri) {
    // NOTE: The returned value may NOT be a local filesystem path.
    // Callers should verify locality (e.g., via isLocal/getFile) before assuming file access.

    if (context == null || uri == null) {
        return null;
    }

    final boolean isAtLeast19 = android.os.Build.VERSION.SDK_INT >= 19;

    // Special-case Google Photos: return the remote address instead of querying _data
    if (isGooglePhotosUri(uri)) {
        return uri.getLastPathSegment();
    }

    // DocumentProvider
    if (isAtLeast19 && android.provider.DocumentsContract.isDocumentUri(context, uri)) {
        // ExternalStorageProvider
        if (isExternalStorageDocument(uri)) {
            final String docId = android.provider.DocumentsContract.getDocumentId(uri);
            if (docId != null) {
                final String[] split = docId.split(":");
                if (split.length >= 2) {
                    final String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        return android.os.Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                }
            }
        }
        // DownloadsProvider
        else if (isDownloadsDocument(uri)) {
            final String id = android.provider.DocumentsContract.getDocumentId(uri);
            if (id != null) {
                try {
                    final long contentId = Long.valueOf(id);
                    final android.net.Uri contentUri = android.content.ContentUris.withAppendedId(
                            android.net.Uri.parse("content://downloads/public_downloads"), contentId);
                    if (contentUri != null) {
                        return getDataColumn(context, contentUri, null, null);
                    }
                } catch (NumberFormatException e) {
                    // Unexpected document id format; fall through and return null
                    return null;
                }
            }
        }
        // MediaProvider
        else if (isMediaDocument(uri)) {
            final String docId = android.provider.DocumentsContract.getDocumentId(uri);
            if (docId != null) {
                final String[] split = docId.split(":");
                if (split.length >= 2) {
                    final String type = split[0];

                    android.net.Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    if (contentUri != null) {
                        final String selection = "_id=?";
                        final String[] selectionArgs = new String[] { split[1] };
                        return getDataColumn(context, contentUri, selection, selectionArgs);
                    }
                }
            }
        }
    }
    // MediaStore (and general)
    else if ("content".equalsIgnoreCase(uri.getScheme())) {
        return getDataColumn(context, uri, null, null);
    }
    // File
    else if ("file".equalsIgnoreCase(uri.getScheme())) {
        return uri.getPath();
    }

    return null;
}