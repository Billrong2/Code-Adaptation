@Override
public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
    Cursor sourceCursor = null;
    try {
        sourceCursor = super.query(uri, projection, selection, selectionArgs, sortOrder);
        if (sourceCursor == null) {
            return null;
        }

        String[] columnNames = sourceCursor.getColumnNames();
        if (columnNames == null) {
            return sourceCursor;
        }

        String[] newColumnNames = columnNamesWithData(columnNames);
        MatrixCursor resultCursor = new MatrixCursor(newColumnNames, sourceCursor.getCount());

        sourceCursor.moveToPosition(-1);
        while (sourceCursor.moveToNext()) {
            MatrixCursor.RowBuilder row = resultCursor.newRow();
            for (int i = 0; i < columnNames.length; i++) {
                row.add(sourceCursor.getString(i));
            }
        }

        return resultCursor;
    } catch (IllegalStateException e) {
        return sourceCursor;
    } finally {
        if (sourceCursor != null && !sourceCursor.isClosed()) {
            sourceCursor.close();
        }
    }
}