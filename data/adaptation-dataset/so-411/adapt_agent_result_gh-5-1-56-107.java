@Override
public void fillWindow(int position, android.database.CursorWindow window) {
    // NOTE: Cross-process cursor wrappers should implement CrossProcessCursor for proper IPC behavior.
    if (window == null) {
        return;
    }
    if (position < 0 || position > getCount()) {
        return;
    }
    window.acquireReference();
    try {
        moveToPosition(position - 1);
        window.clear();
        window.setStartPosition(position);
        final int columnNum = getColumnCount();
        window.setNumColumns(columnNum);
        while (moveToNext() && window.allocRow()) {
            for (int i = 0; i < columnNum; i++) {
                boolean wroteValue = false;
                // Special handling for icon cache BLOB column
                if (i == IDX_ICON_CACHE && IDX_ICON_CACHE >= 0 && IDX_ICON_CACHE < columnNum) {
                    byte[] blob = null;
                    try {
                        blob = getBlob(i);
                    } catch (Exception ignore) {
                        // Keep behavior minimal; fall through to null handling
                    }
                    if (blob != null && blob.length > 0) {
                        if (!window.putBlob(blob, getPosition(), i)) {
                            window.freeLastRow();
                            break;
                        }
                        wroteValue = true;
                    }
                }
                // Default string handling for non-BLOB columns or when BLOB is invalid
                if (!wroteValue) {
                    String field = null;
                    try {
                        field = getString(i);
                    } catch (Exception ignore) {
                        field = null;
                    }
                    if (field != null) {
                        if (!window.putString(field, getPosition(), i)) {
                            window.freeLastRow();
                            break;
                        }
                        wroteValue = true;
                    }
                }
                // Defer null placement until neither blob nor string was written
                if (!wroteValue) {
                    if (!window.putNull(getPosition(), i)) {
                        window.freeLastRow();
                        break;
                    }
                }
            }
        }
    } catch (IllegalStateException e) {
        // simply ignore it
    } finally {
        window.releaseReference();
    }
}