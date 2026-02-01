/**
     * Compatibility helper to determine a column's field type across API levels.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static int getColumnType(final Cursor cursor, final int columnIndex) {
        if (cursor == null || columnIndex < 0) {
            return FIELD_TYPE_NULL;
        }

        // API 11+ has native support
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            try {
                return cursor.getType(columnIndex);
            } catch (RuntimeException e) {
                // fall through to legacy path
            }
        }

        // Legacy fallback for pre-Honeycomb
        int type = FIELD_TYPE_NULL;
        if (cursor instanceof SQLiteCursor) {
            SQLiteCursor sqLiteCursor = (SQLiteCursor) cursor;
            CursorWindow cursorWindow = sqLiteCursor.getWindow();
            if (cursorWindow != null) {
                int pos = cursor.getPosition();
                if (pos >= 0) {
                    if (cursorWindow.isNull(pos, columnIndex)) {
                        type = FIELD_TYPE_NULL;
                    } else if (cursorWindow.isLong(pos, columnIndex)) {
                        type = FIELD_TYPE_INTEGER;
                    } else if (cursorWindow.isFloat(pos, columnIndex)) {
                        type = FIELD_TYPE_FLOAT;
                    } else if (cursorWindow.isString(pos, columnIndex)) {
                        type = FIELD_TYPE_STRING;
                    } else if (cursorWindow.isBlob(pos, columnIndex)) {
                        type = FIELD_TYPE_BLOB;
                    }
                }
            }
        }

        return type;
    }