@Override
public Cursor query(final Uri uri, final String[] projection, final String selection, final String[] selectionArgs, final String sortOrder) {
    final Cursor source = super.query(uri, projection, selection, selectionArgs, sortOrder);
    if (source == null) {
        Log.w(LOG_TAG, "super.query(...) returned null; returning null cursor");
        return null;
    }

    final String[] columnNames = source.getColumnNames();
    final String[] newColumnNames = columnNamesWithData(columnNames);
    final MatrixCursor cursor = new MatrixCursor(newColumnNames, source.getCount());

    try {
        source.moveToPosition(-1);
        while (source.moveToNext()) {
            final MatrixCursor.RowBuilder row = cursor.newRow();
            for (int i = 0; i < columnNames.length; i++) {
                row.add(source.getString(i));
            }
        }
    } finally {
        source.close();
    }

    return cursor;
}