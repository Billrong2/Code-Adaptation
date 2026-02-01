@TargetApi(Build.VERSION_CODES.KITKAT)
    private String handleKitKat(final Context context, final Uri uri) {
        // Helper for KitKat+ DocumentProvider URIs only; returns null otherwise
        if (context == null || uri == null) {
            return null;
        }

        if (!DocumentsContract.isDocumentUri(context, uri)) {
            return null;
        }

        try {
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
                // non-primary volumes not supported
                return null;
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                if (id == null) {
                    return null;
                }
                try {
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                    return getDataColumn(context, contentUri, null, null);
                } catch (NumberFormatException nfe) {
                    return null;
                }
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
                final String[] selectionArgs = new String[]{ split[1] };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } catch (RuntimeException ex) {
            // swallow and return null for any unexpected framework issues
            return null;
        }

        return null;
    }