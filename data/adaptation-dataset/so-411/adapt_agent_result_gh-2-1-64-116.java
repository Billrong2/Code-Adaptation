/**
     * Fills a {@link android.database.CursorWindow} for cross-process access.
     * <p>
     * Note: cross-process cursor wrappers should implement {@link android.database.CrossProcessCursor}
     * and provide a proper {@code fillWindow} implementation.
     */
    @Override
    public void fillWindow(int position, android.database.CursorWindow window) {
        if (position < 0 || position > getCount() || window == null) {
            return;
        }
        // Scaffold for an optional special BLOB column (e.g., icon cache). If not present, keep disabled.
        final int IDX_ICON_CACHE = -1; // unknown / optional

        window.acquireReference();
        try {
            moveToPosition(position - 1);
            window.clear();
            window.setStartPosition(position);
            final int columnNum = getColumnCount();
            window.setNumColumns(columnNum);
            while (moveToNext() && window.allocRow()) {
                final int rowPos = getPosition();
                for (int col = 0; col < columnNum; col++) {
                    boolean wroteValue = false;

                    // Special-case BLOB handling if the column exists
                    if (IDX_ICON_CACHE >= 0 && col == IDX_ICON_CACHE) {
                        byte[] blob = null;
                        try {
                            blob = getBlob(col);
                        } catch (Exception ignored) {
                            // Intentionally ignored; fall back to NULL handling
                        }
                        if (blob != null && blob.length > 0) {
                            wroteValue = window.putBlob(blob, rowPos, col);
                        }
                    } else {
                        String value = null;
                        try {
                            value = getString(col);
                        } catch (Exception ignored) {
                            // Intentionally ignored; fall back to NULL handling
                        }
                        if (value != null) {
                            wroteValue = window.putString(value, rowPos, col);
                        }
                    }

                    if (!wroteValue) {
                        if (!window.putNull(rowPos, col)) {
                            window.freeLastRow();
                            break;
                        }
                    }
                }
            }
        } catch (IllegalStateException e) {
            // Simply ignore it, preserving existing behavior
        } finally {
            window.releaseReference();
        }
    }